const shader = `
@group(0) @binding(0) var<storage, read> input : array<u32>;
@group(0) @binding(1) var<storage, read_write> output: array<i32>;

// input data is:  count, maxIterations, chunks, y (chunks bytes), xs (count*chunks bytes)

const MAX_CHUNKS = 25;

var<private> x : array<u32,MAX_CHUNKS>;
var<private> y : array<u32,MAX_CHUNKS>;
var<private> zx : array<u32,MAX_CHUNKS>;
var<private> zy : array<u32,MAX_CHUNKS>;
var<private> work1 : array<u32,MAX_CHUNKS>;
var<private> work2 : array<u32,MAX_CHUNKS>;
var<private> work3 : array<u32,MAX_CHUNKS>;

var<private> chunks : i32;

@compute @workgroup_size(64)
fn main(  @builtin(global_invocation_id) global_id : vec3u ) {
    if (global_id.x >= input[0]) {
      return;
    }
    let maxIterations : u32 = input[1];
    chunks = i32(input[2]);
    let xstart : i32 = (1 + i32(global_id.x)) * chunks + 3;
    for (var i : i32 = 0; i < chunks ; i++) {
      x[i] = input[xstart+i];
      y[i] = input[3+i];
      zx[i] = x[i];
      zy[i] = y[i];
    }
    var iter : u32 = 0;
    while (iter < maxIterations) {
        arraycopy(&zx, &work2);
        multiply(&work2,&zx);  // work2 = zx*zx
        arraycopy(&zy, &work1);
        multiply(&work1,&zy);  // work1 = zy*zy
        arraycopy(&work1,&work3);   // work3 = zy*zy, save a copy.  (Note: multiplication uses work3.)
        add(&work1,&work2);  // work1 = zx*zx + zy*zy
        if ((work1[0] & 0xFFF8) != 0 && (work1[0] & 0xFFF8) != 0xFFF0) {
            break;
        }
        negate(&work3);  // work3 = -work3 = -zy*zy
        add(&work2,&work3);  // work2 = zx*zx - zy*zy
        add(&work2,&x); // work2 = zx*zx - zy*zy + x, the next value for zx
        arraycopy(&zx,&work1);  // work1 = zx
        add(&work1,&zx);  // work1 = 2*zx
        multiply(&work1,&zy);  // work1 = 2*zx*zy
        add(&work1,&y);  // work1 = 2*zx*zy + y, the next value for zy
        arraycopy(&work1,&zy);  // zy = work1
        arraycopy(&work2,&zx);  // zx = work2
        iter++;
    }
    if (iter < maxIterations) {
       output[global_id.x] = i32(iter);
    }
    else {
       output[global_id.x] = -1;
    }
}


fn arraycopy( sourceArray : ptr<private,array<u32,MAX_CHUNKS>>, destArray : ptr<private,array<u32,MAX_CHUNKS>> ) {
   for (var i : i32 = 0; i < chunks; i++) {
       (*destArray)[i] = (*sourceArray)[i];
   } 
}

fn add( x : ptr<private,array<u32,MAX_CHUNKS>>, dx : ptr<private,array<u32,MAX_CHUNKS>>) {
    var carry : u32  = 0;
    for (var i : i32 = chunks - 1; i >= 0; i--) {
        (*x)[i] += (*dx)[i];
        (*x)[i] += carry;
        carry = (*x)[i] >> 16;
        (*x)[i] &= 0xFFFF;
    }
}

fn multiply( x : ptr<private,array<u32,MAX_CHUNKS>>, y : ptr<private,array<u32,MAX_CHUNKS>>){  // Can't allow x == y !
    var neg1 : bool = ((*x)[0] & 0x8000) != 0;
    if (neg1) {
        negate(x);
    }
    var neg2 : bool = ((*y)[0] & 0x8000) != 0;
    if (neg2) {
        negate(y);
    }
    if ((*x)[0] == 0) {
        for (var i: i32  = 0; i < chunks; i++) {
            work3[i] = 0;
        }
    }
    else {
        var carry : u32 = 0;
        for (var i : i32 = chunks-1; i >= 0; i--) {
            work3[i] = (*x)[0]*(*y)[i] + carry;
            carry = work3[i] >> 16;
            work3[i] &= 0xFFFF;
        }
    }
    for (var j = 1; j < chunks; j++) {
        var i : i32 = chunks - j;
        var carry : u32 = ((*x)[j]*(*y)[i]) >> 16;
        i--;
        var k = chunks - 1;
        while (i >= 0) {
            work3[k] += (*x)[j]*(*y)[i] + carry;
            carry = work3[k] >> 16;
            work3[k] &= 0xFFFF;
            i--;
            k--;
        }
        while (carry != 0 && k >= 0) {
            work3[k] += carry;
            carry = work3[k] >> 16;
            work3[k] &= 0xFFFF;
            k--;
        }
    }
    arraycopy(&work3,x);
    if (neg2) {
        negate(y);
    }
    if (neg1 != neg2) {
        negate(x);
    }
}

fn negate( x : ptr<private,array<u32,MAX_CHUNKS>> ) {
    for (var i : i32 = 0; i < chunks; i++) {
        (*x)[i] = 0xFFFF-(*x)[i];
    }
    (*x)[chunks-1]++;
    for (var i = chunks-1; i > 0 && ((*x)[i] & 0x10000) != 0; i--) {
        (*x)[i] &= 0xFFFF;
        (*x)[i-1]++;
    }
    (*x)[0] &= 0xFFFF;
}
`;


var /* BigDecimal */ xmin, dx, yval;
var /* int */ columnCount, rowNumber, maxIterations;
var /* boolean */ highPrecision;

var /* int */ taskNumber, jobNumber, workerNumber;

var /* int[] */ iterationCounts;

var ArrayType = this.Uint32Array || Array;



onmessage = async function(msg) {
   var data = msg.data;
   //console.log("received message", data);
   if (data == "init") {
      await setupGPU();
   }
   else if ( data[0] == "setup" ) { 
       jobNumber = data[1];
       maxIterations = data[2];
       highPrecision = data[3];
       workerNumber = data[4];
       jobIsSetUp = false;
   }
   else if ( data[0] == "task" ) {
       taskNumber = data[1];
       columnCount = data[2];
       xmin = data[3];
       dx = data[4];
       yval = data[5];
       createHPData();
       if (!jobIsSetUp) {
          try {
             console.log("calling setUpJob");
             await setUpJob();
             console.log("returned from setUpJob");
             jobIsSetUp = true;
          }
          catch (e) {
             postMessage(["gpufail",taskNumber,e.message]);
             return;
          }
       }
       iterationCounts = new Array(columnCount);
       var output;
       try {
           //console.log("caling doTask");
           output = await doTask();
           //console.log("returned from doTask", output);
       }
       catch (e) {
           postMessage(["gpufail",taskNumber,e.message]);
       }
       
       iterationCounts = new Array(columnCount);
       for (var i = 0; i < columnCount; i++)
          iterationCounts[i] = output[i];
       postMessage([ jobNumber, taskNumber, iterationCounts, workerNumber ]);
   }
}


let jobIsSetUp;
let device;
let shaderModule;
let outputBuffer;
let stagingBuffer = null;
let outputBufferSize;
let inputBuffer = null;
let inputBufferSize;
let computePipeline;
let bindGroup;
let chunks;
let inputDataArray = null;
let gpuError = null;

async function setupGPU() {
  if (!navigator.gpu) {
    postMessage(["error","WebGPU is not implemented!"]);
    return;
  }
  const adapter = await navigator.gpu.requestAdapter();
  if (!adapter) {
    postMessage(["error","WebGPU is implemented, but no adapter is available."]);
    return;
  }
  device = await adapter.requestDevice();
  device.pushErrorScope("validation");
  shaderModule = device.createShaderModule({
    code: shader
  });
  device.popErrorScope().then((error) => {
    if (error) {
      postMessage(["error","WebGPU device was created, but there was an error in creating the shader."]);
      return;
    }
  });
  device.addEventListener('uncapturederror', (event) => { gpuError = event.error; console.log("gpuError", event.error); } );
  postMessage(["ok"]);
}

async function setUpJob() {
   gpuError = null;
   console.log("in setup job");
   if (stagingBuffer === null || outputBufferSize !== 4*columnCount) {
      if (stagingBuffer !== null) {
         outputBuffer.destroy();
         stagingBuffer.destroy();
      }
      console.log("creating output buffers", columnCount);
      outputBufferSize = 4*columnCount;
      outputBuffer = device.createBuffer({
        size: outputBufferSize,
        usage: GPUBufferUsage.STORAGE | GPUBufferUsage.COPY_SRC
      });
      stagingBuffer = device.createBuffer({
        size: outputBufferSize,
        usage: GPUBufferUsage.MAP_READ | GPUBufferUsage.COPY_DST
      });
      console.log("Created output buffer ", outputBuffer,stagingBuffer);
   }
   inputCt = 3 + (columnCount + 1)*chunks;
   if (inputBuffer === null || inputBufferSize !== 4*inputCt) {
      if (inputBuffer !== null)
         inputBuffer.destroy();
      inputBufferSize = 4*inputCt;
      inputBuffer = device.createBuffer({
        size: inputBufferSize,
        usage: GPUBufferUsage.STORAGE | GPUBufferUsage.COPY_DST
      });
      console.log("Created input buffer ", inputBuffer);
   }
   
    let bindGroupLayout = device.createBindGroupLayout({
      entries: [
        {
          binding: 0,
          visibility: GPUShaderStage.COMPUTE,
          buffer: {
            type: "read-only-storage"
          }
        },
        {
          binding: 1,
          visibility: GPUShaderStage.COMPUTE,
          buffer: {
            type: "storage"
          }
        }
      ]
    });

    bindGroup = device.createBindGroup({
      layout: bindGroupLayout,
      entries: [
        {
          binding: 0,
          resource: {
            buffer: inputBuffer
          }
        },
        {
          binding: 1,
          resource: {
            buffer: outputBuffer
          }
        }
      ]
    });   
   console.log("created bind group", bindGroup);
   
   computePipeline = device.createComputePipeline({
        layout: device.createPipelineLayout({
            bindGroupLayouts: [bindGroupLayout]
        }),
        compute: {
          module: shaderModule,
          entryPoint: "main"
        }
   });
   console.log("Created pipeline.", computePipeline);
   if (gpuError !== null)
      throw new Error("GPU error: " + gpuError);
}

async function doTask() {
   //console.log("in doTask", inputBuffer, inputDataArray, inputBufferSize);
   gpuError = null;
   device.queue.writeBuffer(inputBuffer,0,inputDataArray,0,inputDataArray.length);
   //console.log("wrote input buffer");
   var commandEncoder = device.createCommandEncoder();
   var passEncoder = commandEncoder.beginComputePass();
   passEncoder.setPipeline(computePipeline);
   passEncoder.setBindGroup(0, bindGroup);
   passEncoder.dispatchWorkgroups(Math.ceil(columnCount / 64));
   passEncoder.end();
   commandEncoder.copyBufferToBuffer(outputBuffer,0,stagingBuffer,0,outputBufferSize);
   device.queue.submit([commandEncoder.finish()]);
   //console.log("submitted job");
   await stagingBuffer.mapAsync(GPUMapMode.READ, 0, outputBufferSize);
   //console.log("mapped output buffer");
   var copyBuffer = stagingBuffer.getMappedRange(0, outputBufferSize);
   var outputData = copyBuffer.slice();
   stagingBuffer.unmap();
   if (gpuError !== null)
      throw new Error("GPU error: " + gpuError);
   return new Int32Array(outputData);
}


function createHPData() {
    chunks = xmin.length - 1;
    xs = new Array(columnCount);
    xs[0] = xmin; 
    for (var i = 1; i < columnCount; i++) {
        xs[i] = new ArrayType(chunks+1);
    }
    if (columnCount > 1) {
        for (var i = 1; i < columnCount; i++) {
            for (var j = 0; j <= chunks; j++) {
                xs[i][j] = xs[i-1][j];
            }
            add(xs[i],dx,chunks+1);
        }
    }
    if (inputDataArray === null || inputDataArray.length !== 3 + (columnCount+1)*chunks)
        inputDataArray = new Uint32Array(3 + (columnCount+1)*chunks);
    inputDataArray[0] = columnCount;
    inputDataArray[1] = maxIterations;
    inputDataArray[2] = chunks;
    for (let i = 0; i < chunks; i++)
       inputDataArray[i+3] = yval[i];
    for (let i = 0; i < columnCount; i++) {
       let xStart = 3 + (i+1)*chunks;
       for (let j = 0; j < chunks; j++)
          inputDataArray[xStart+j] = xs[i][j];
    }
    function add( /* int[] */ x, /* int[] */ dx, /* int */ count) {
        var carry = 0;
        for (var i = count - 1; i >= 0; i--) {
            x[i] += dx[i];
            x[i] += carry;
            carry = x[i] >>> 16;
            x[i] &= 0xFFFF;
        }
    }
}


