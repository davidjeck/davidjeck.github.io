// Clear color for GPURenderPassDescriptor
const clearColor = { r: 0.0, g: 0.5, b: 1.0, a: 1.0 };  // (not really used)

// Vertex data for drawing the entire coordinate rectangle

const vertices = new Float32Array([
  -1, -1, 0, 1,   1, 0, 0,
  1, -1, 0, 1,   0, 1, 0,
  -1, 1, 0, 1,   0, 0, 1,
  1, 1, 0, 1,   0, 0, 1,
]);

// Vertex and fragment shaders

const shaders = `
struct MandelbrotData {
   xmin : f32,
   xmax : f32,
   ymin : f32,
   ymax : f32,
   width : f32,
   height : f32,
   maxIterations : i32,
   paletteLength : i32
}

@group(0) @binding(0) var<uniform> mbData : MandelbrotData;

@vertex
fn vertex_main(@location(0) position: vec4f) ->  @builtin(position) vec4f
{
    return position;
}

@fragment
fn fragment_main(@builtin(position) coord: vec4f) -> @location(0) vec4f
{
  var x: f32 = mbData.xmin + (mbData.xmax-mbData.xmin)*coord.x/mbData.width;
  var y: f32 = mbData.ymin + (mbData.ymax-mbData.ymin)*(mbData.height-coord.y)/mbData.height;
    var count : i32 = 0;
    var zx : f32 = x;
    var zy : f32 = y;
    while (count < mbData.maxIterations && zx*zx + zy*zy < 8.0) {
        let new_zx : f32 = zx*zx - zy*zy + x;
        zy = 2*zx*zy + y;
        zx = new_zx;
        count++;
    }
    if (count >= mbData.maxIterations) {
       return vec4f(0,0,0,1);
    }
    else {
       let colorNum = count % mbData.paletteLength;
       let gray = f32(colorNum)/f32(mbData.paletteLength);
       return vec4f(gray,gray,gray,1);
    }
}
`;


const hpShaders = `
@group(0) @binding(0) var<storage, read> input : array<u32>;

// input data is:  maxIterations, paletteLength, chunks, minx, dx, maxy, dy (chunks bytes for each of x, dx, y, dy)

const MAX_CHUNKS = 50;


var<private> chunks : i32;


@vertex
fn vertex_main(@location(0) position: vec4f) ->  @builtin(position) vec4f
{
    return position;
}

@fragment
fn fragment_main(@builtin(position) coord: vec4f) -> @location(0) vec4f
{
    var work1 : array<u32,MAX_CHUNKS>;
    var x : array<u32,MAX_CHUNKS>;
    var y : array<u32,MAX_CHUNKS>;
    var zx : array<u32,MAX_CHUNKS>;
    var zy : array<u32,MAX_CHUNKS>;
    var work2 : array<u32,MAX_CHUNKS>;
    var work3 : array<u32,MAX_CHUNKS>;
    let maxIterations : u32 = input[0];
    let paletteLength : u32 = input[1];
    chunks = i32(input[2]);
    for (var i : i32 = 0; i < chunks ; i++) {
      x[i] = input[3+i]; // minx
      work1[i] = input[3+chunks+i];  // dx
      y[i] = input[3+2*chunks+i];  // maxy
      work2[i] = input[3+3*chunks+i]; //dy
    }
    multiplyInt(&work1, u32(coord.x));
    add(&x,&work1);  // x = minx + (pixel_x_coord)*dx
    multiplyInt(&work2, u32(coord.y));
    negate(&work2);
    add(&y,&work2);  // y = maxy - (pixel_y_coord)*dy
    arraycopy(&x,&zx);
    arraycopy(&y,&zy);
    var iter : u32 = 0;
    while (iter < maxIterations) {
        arraycopy(&zx, &work2);
        multiply(&work2,&zx,&work3);  // work2 = zx*zx
        arraycopy(&zy, &work1);
        multiply(&work1,&zy,&work3);  // work1 = zy*zy
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
        multiply(&work1,&zy,&work3);  // work1 = 2*zx*zy
        add(&work1,&y);  // work1 = 2*zx*zy + y, the next value for zy
        arraycopy(&work1,&zy);  // zy = work1
        arraycopy(&work2,&zx);  // zx = work2
        iter++;
    }
    if (iter >= maxIterations) {
       return vec4f(0,0,0,1);
    }
    else {
       let colorNum = iter % paletteLength;
       let gray = f32(colorNum)/f32(paletteLength);
       return vec4f(gray,gray,gray,1);
    }
}


fn arraycopy( sourceArray : ptr<function,array<u32,MAX_CHUNKS>>, destArray : ptr<function,array<u32,MAX_CHUNKS>> ) {
   for (var i : i32 = 0; i < chunks; i++) {
       (*destArray)[i] = (*sourceArray)[i];
   } 
}

fn add( x : ptr<function,array<u32,MAX_CHUNKS>>, dx : ptr<function,array<u32,MAX_CHUNKS>>) {
    var carry : u32  = 0;
    for (var i : i32 = chunks - 1; i >= 0; i--) {
        (*x)[i] += (*dx)[i];
        (*x)[i] += carry;
        carry = (*x)[i] >> 16;
        (*x)[i] &= 0xFFFF;
    }
}

fn multiplyInt(x : ptr<function,array<u32,MAX_CHUNKS>>, n : u32) { // multiply by integer less than 2^16
   var carry : u32 = 0;
    for (var i : i32 = chunks - 1; i >= 0; i--) {
        (*x)[i] *= n;
        (*x)[i] += carry;
        carry = (*x)[i] >> 16;
        (*x)[i] &= 0xFFFF;
    }
}

fn multiply( x : ptr<function,array<u32,MAX_CHUNKS>>, y : ptr<function,array<u32,MAX_CHUNKS>>, work3 : ptr<function,array<u32,MAX_CHUNKS>>){  // Can't allow x == y !
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
            (*work3)[i] = 0;
        }
    }
    else {
        var carry : u32 = 0;
        for (var i : i32 = chunks-1; i >= 0; i--) {
            (*work3)[i] = (*x)[0]*(*y)[i] + carry;
            carry = (*work3)[i] >> 16;
            (*work3)[i] &= 0xFFFF;
        }
    }
    for (var j = 1; j < chunks; j++) {
        var i : i32 = chunks - j;
        var carry : u32 = ((*x)[j]*(*y)[i]) >> 16;
        i--;
        var k = chunks - 1;
        while (i >= 0) {
            (*work3)[k] += (*x)[j]*(*y)[i] + carry;
            carry = (*work3)[k] >> 16;
            (*work3)[k] &= 0xFFFF;
            i--;
            k--;
        }
        while (carry != 0 && k >= 0) {
            (*work3)[k] += carry;
            carry = (*work3)[k] >> 16;
            (*work3)[k] &= 0xFFFF;
            k--;
        }
    }
    arraycopy(work3,x);
    if (neg2) {
        negate(y);
    }
    if (neg1 != neg2) {
        negate(x);
    }
}

fn negate( x : ptr<function,array<u32,MAX_CHUNKS>> ) {
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



// Main function

const HP_CUTOFF_EXP = 7;
const HP_CUTOFF = new BigDecimal("1e-7");
const TEN = new BigDecimal("10");
const TWO = new BigDecimal("2");
const log2of10 =  Math.log(10)/Math.log(2);
const twoTo16 = new BigDecimal("65536");

let xmin = new BigDecimal("-2.2");
let xmax = new BigDecimal("0.8");
let ymin = new BigDecimal("-1.2"); 
let ymax = new BigDecimal("1.2");

async function init() {
  // 1: request adapter and device
  if (!navigator.gpu) {
    throw Error('WebGPU not supported.');
  }

  const adapter = await navigator.gpu.requestAdapter();
  if (!adapter) {
    throw Error('Couldn\'t request WebGPU adapter.');
  }

  let device = await adapter.requestDevice();

  // 2: Create a shader module from the shaders template literal
  const shaderModule = device.createShaderModule({
    code: shaders
  });
  
 device.lost.then((info) => {
    alert(`WebGPU device was lost: ${info.message}`);
  });
    
  // 3: Get reference to the canvas to render on
  const canvas = document.querySelector('#gpuCanvas');
  const context = canvas.getContext('webgpu');

  context.configure({
    device: device,
    format: navigator.gpu.getPreferredCanvasFormat(),
    alphaMode: 'premultiplied'
  });
  
  // Create uniform buffer for MandelbrotData
  const mbDataBuffer = device.createBuffer( {
     size: 32,
     usage: GPUBufferUsage.UNIFORM | GPUBufferUsage.COPY_DST
  });
  
  const mbData = new ArrayBuffer(32);
  const mbView = new DataView(mbData);
  mbView.setFloat32(0,-2.2,true);
  mbView.setFloat32(4,0.8,true);
  mbView.setFloat32(8,-1.2,true);
  mbView.setFloat32(12,1.2,true);
  mbView.setFloat32(16,800,true);
  mbView.setFloat32(20,600,true);
  mbView.setInt32(24,1000,true);
  mbView.setInt32(28,50,true);

  // 4: Create vertex buffer to contain vertex data
  const vertexBuffer = device.createBuffer({
    size: vertices.byteLength, // make it big enough to store vertices in
    usage: GPUBufferUsage.VERTEX | GPUBufferUsage.COPY_DST,
  });
  

  // Copy the vertex data over to the GPUBuffer using the writeBuffer() utility function
  device.queue.writeBuffer(vertexBuffer, 0, vertices, 0, vertices.length);
  
  const bindGroupLayout = device.createBindGroupLayout({
    entries: [{
      binding: 0,
      visibility: GPUShaderStage.FRAGMENT,
      buffer: {
        type: "uniform"
      }
    }]
  });

  const bindGroup = device.createBindGroup({
    layout: bindGroupLayout,
    entries: [{
      binding: 0,
      resource: {
        buffer: mbDataBuffer,
      }
    }]
  });

  // 5: Create a GPUVertexBufferLayout and GPURenderPipelineDescriptor to provide a definition of our render pipline
  const vertexBuffers = [{
    attributes: [{
      shaderLocation: 0, // position
      offset: 0,
      format: 'float32x4'
    }, {
      shaderLocation: 1, // color
      offset: 16,
      format: 'float32x3'
    }],
    arrayStride: 28,
    stepMode: 'vertex'
  }];

  const pipelineDescriptor = {
    vertex: {
      module: shaderModule,
      entryPoint: 'vertex_main',
      buffers: vertexBuffers
    },
    fragment: {
      module: shaderModule,
      entryPoint: 'fragment_main',
      targets: [{
        format: navigator.gpu.getPreferredCanvasFormat()
      }]
    },
    primitive: {
      topology: 'triangle-strip'
    },
//    layout: 'auto'
    layout: device.createPipelineLayout({
      bindGroupLayouts: [bindGroupLayout]
    }),
  };

  // 6: Create the actual render pipeline

  const renderPipeline = device.createRenderPipeline(pipelineDescriptor);
  
  //------------------ HP stuff ----------------------------------
  
    const hpShaderModule = device.createShaderModule({
     code: hpShaders
  });

  const hpDataBuffer = device.createBuffer( {
     size: 4*(3 + 50*4), // large enough for chunks = MAX_CHUNKS
     usage: GPUBufferUsage.STORAGE | GPUBufferUsage.COPY_DST
  });
  
  const hpData = new Uint32Array(3*50*4);
  hpData[0] = 100; // maxIterations
  hpData[1] = 50;   // paletteLength
    
  const bindGroupLayoutHP = device.createBindGroupLayout({
    entries: [{
      binding: 0,
      visibility: GPUShaderStage.FRAGMENT,
      buffer: {
        type: "read-only-storage"
      }
    }]
  });

  const bindGroupHP = device.createBindGroup({
    layout: bindGroupLayoutHP,
    entries: [{
      binding: 0,
      resource: {
        buffer: hpDataBuffer,
      }
    }]
  });


  const pipelineDescriptorHP = {
    vertex: {
      module: hpShaderModule,
      entryPoint: 'vertex_main',
      buffers: vertexBuffers
    },
    fragment: {
      module: hpShaderModule,
      entryPoint: 'fragment_main',
      targets: [{
        format: navigator.gpu.getPreferredCanvasFormat()
      }]
    },
    primitive: {
      topology: 'triangle-strip'
    },
//    layout: 'auto'
    layout: device.createPipelineLayout({
      bindGroupLayouts: [bindGroupLayoutHP]
    }),
  };

  // 6: Create the actual render pipeline

  const renderPipelineHP = device.createRenderPipeline(pipelineDescriptorHP);

  
  //--------------------------------------------------------------

  draw();
  
  canvas.onmousedown = function(evt) {  // zoom out by a factor of 2

      let r = canvas.getBoundingClientRect();
      let x = evt.clientX - r.left;
      let y = evt.clientY - r.top;
 
      zoom(x,y,evt.shiftKey ? 2 : 0.5,false);
      
      draw();

  };
  
  function draw() {
    if (xmin.scale() < HP_CUTOFF_EXP + 8)
        xmin = xmin.setScale(HP_CUTOFF_EXP + 8);
    if (xmax.scale() < HP_CUTOFF_EXP + 8)
        xmax = xmax.setScale(HP_CUTOFF_EXP + 8);
    if (ymin.scale() < HP_CUTOFF_EXP + 8)
        ymin = ymin.setScale(HP_CUTOFF_EXP + 8);
    if (ymax.scale() < HP_CUTOFF_EXP + 8)
        ymax = ymax.setScale(HP_CUTOFF_EXP + 8);
    var dx = xmax.subtract(xmin).setScale(Math.max(xmax.scale(),HP_CUTOFF_EXP)*2, BigDecimal.ROUND_HALF_EVEN);
    dx = dx.divide(new BigDecimal("" + canvas.width),BigDecimal.ROUND_HALF_EVEN);
    var precision = 0;
    while (dx.compareTo(TWO) < 0) {
       precision++;
       dx = dx.multiply(TEN);
    }
    if (precision < HP_CUTOFF_EXP)
        precision = HP_CUTOFF_EXP;
    var scale = precision + 5 + Math.floor((precision-10)/10);
    xmin = xmin.setScale(scale,BigDecimal.ROUND_HALF_EVEN);
    xmax = xmax.setScale(scale,BigDecimal.ROUND_HALF_EVEN);
    ymin = ymin.setScale(scale,BigDecimal.ROUND_HALF_EVEN);
    ymax = ymax.setScale(scale,BigDecimal.ROUND_HALF_EVEN);
     dx = xmax.subtract(xmin).divide(new BigDecimal(""+(canvas.width-1)),BigDecimal.ROUND_HALF_EVEN);
     dy = ymax.subtract(ymin).divide(new BigDecimal(""+(canvas.height-1)),BigDecimal.ROUND_HALF_EVEN);
     highPrecision = dy.compareTo(HP_CUTOFF) < 0;
     if (highPrecision)
        drawHighP();
     else
        drawNormalP();  
  }
  
  function drawNormalP() {
      console.log("\nStart NormalP draw");

        var xmin_d = Number(xmin.toString());
        var xmax_d = Number(xmax.toString());
        var ymin_d = Number(ymin.toString());
        var ymax_d = Number(ymax.toString());
        mbView.setFloat32(0,xmin_d,true);
        mbView.setFloat32(4,xmax_d,true);
        mbView.setFloat32(8,ymin_d,true);
        mbView.setFloat32(12,ymax_d,true);

      device.queue.writeBuffer(mbDataBuffer,0,mbData,0,32);

      // 7: Create GPUCommandEncoder to issue commands to the GPU
      // Note: render pass descriptor, command encoder, etc. are destroyed after use, fresh one needed for each frame.
      const commandEncoder = device.createCommandEncoder();

      // 8: Create GPURenderPassDescriptor to tell WebGPU which texture to draw into, then initiate render pass

      const renderPassDescriptor = {
        colorAttachments: [{
          clearValue: clearColor,
          loadOp: 'clear',
          storeOp: 'store',
          view: context.getCurrentTexture().createView()
        }]
      };

     const passEncoder = commandEncoder.beginRenderPass(renderPassDescriptor);
      // 9: Draw the triangle

      passEncoder.setPipeline(renderPipeline);
      passEncoder.setVertexBuffer(0, vertexBuffer);
      passEncoder.setBindGroup(0, bindGroup);
      passEncoder.draw(4);

      // End the render pass
      passEncoder.end();
      
      //console.log("Created passEncoder");

      // 10: End frame by passing array of command buffers to command queue for execution
      device.queue.submit([commandEncoder.finish()]);
  }
  
  function drawHighP() {
      console.log("\nStart HP draw");

        let digits = xmin.scale();
        let dx = xmax.subtract(xmin).divide(new BigDecimal(""+(canvas.width-1)),BigDecimal.ROUND_HALF_EVEN);
        let dy = ymax.subtract(ymin).divide(new BigDecimal(""+(canvas.height-1)),BigDecimal.ROUND_HALF_EVEN);
        let chunks = Math.floor((digits * log2of10)/16 + 2);
        let dxArray = new Uint32Array(chunks);
        let xminArray = new Uint32Array(chunks);
        convert(xminArray, xmin, chunks);
        convert(dxArray,dx,chunks);
        let dyArray = new Uint32Array(chunks);
        let ymaxArray = new Uint32Array(chunks);
        convert(ymaxArray, ymax, chunks);
        convert(dyArray,dy,chunks);
 
// console.log( xmin.toString(), dx.toString(), ymax.toString(), dy.toString() );
 
      hpData[2] = chunks;
      hpData.set(xminArray,3);
      hpData.set(dxArray,3+chunks);
      hpData.set(ymaxArray,3+2*chunks);
      hpData.set(dyArray,3+3*chunks);
      
//console.log(hpData);

      device.queue.writeBuffer(hpDataBuffer,0,hpData,0,3+4*chunks);


      const commandEncoder = device.createCommandEncoder();

      const renderPassDescriptor = {
        colorAttachments: [{
          clearValue: clearColor,
          loadOp: 'clear',
          storeOp: 'store',
          view: context.getCurrentTexture().createView()
        }]
      };

     const passEncoder = commandEncoder.beginRenderPass(renderPassDescriptor);

      passEncoder.setPipeline(renderPipelineHP);
      passEncoder.setVertexBuffer(0, vertexBuffer);
      passEncoder.setBindGroup(0, bindGroupHP);
      passEncoder.draw(4);

      passEncoder.end();
      
      device.queue.submit([commandEncoder.finish()]);
  }
  
  
}

try {
   init();
}
catch (e) {
   alert("Setup error: " + e.message);
}




function zoom(x, y, zoomFactor, recenter) {  // (x,y) is center of zoom, in pixels; recenter moves that point to center of image
    var zf = new BigDecimal("" + zoomFactor);
    var X = new BigDecimal("" + Math.round(x));
    var Y = new BigDecimal("" + Math.round(y));
    var ImageWidth = new BigDecimal("" + 800); // canvas.width);
    var ImageHeight = new BigDecimal("" + 600); //canvas.height);
    var oldWidth = xmax.subtract(xmin);
    var oldHeight = ymax.subtract(ymin);
    var newWidth = oldWidth.multiply(zf);
    var newHeight = oldHeight.multiply(zf);
    if (newWidth.compareTo(new BigDecimal("100")) > 0) {
        document.getElementById("status").innerHTML =
            "Zooming out that far would reduce the whole Mandelbrot set to a dot.  Ignored.";
        return;
    }
    var pixelWidth = newWidth.divide(ImageWidth,BigDecimal.ROUND_HALF_EVEN);
    var pixelHeight = newHeight.divide(ImageHeight,BigDecimal.ROUND_HALF_EVEN);
    var centerX = xmin.add(X.multiply(oldWidth).divide(ImageWidth,BigDecimal.ROUND_HALF_EVEN));
    var centerY = ymax.subtract(Y.multiply(oldHeight).divide(ImageHeight,BigDecimal.ROUND_HALF_EVEN));
    var newXmin,newXmax,newYmin,newYmax;
    if (recenter) {
        newXmin = centerX.subtract(newWidth.divide(TWO,BigDecimal.ROUND_HALF_EVEN));
        newYmax = centerY.add(newHeight.divide(TWO,BigDecimal.ROUND_HALF_EVEN));
    }
    else {
        newXmin = centerX.subtract(X.multiply(pixelWidth));
        newYmax = centerY.add(Y.multiply(pixelHeight));
    }
    newYmin = newYmax.subtract(newHeight);
    newXmax = newXmin.add(newWidth);
    xmin = newXmin;
    xmax = newXmax;
    ymin = newYmin;
    ymax = newYmax;
}

function convert( /* int[] */ x, /* BigDecimal */ X, /* int */ count) {
    var neg = false;
    if (X.signum() == -1) {
        neg = true;
        X = X.negate();
    }
    x[0] = Number(X.setScale(0,BigDecimal.ROUND_DOWN).toString());
    for (var i = 1; i < count; i++) {
        X = X.subtract(new BigDecimal(""+x[i-1]));
        X = X.multiply(twoTo16);
        x[i] = Number(X.setScale(0,BigDecimal.ROUND_DOWN).toString());
    }
    if (neg) {
        negate(x,count);
    }
    function negate( /* int[] */ x, /* int */ chunks) {
        for (var i = 0; i < chunks; i++)
            x[i] = 0xFFFF-x[i];
        ++x[chunks-1];
        for (var i = chunks-1; i > 0 && (x[i] & 0x10000) != 0; i--) {
            x[i] &= 0xFFFF;
            ++x[i-1];
        }
        x[0] &= 0xFFFF;
    }
}

