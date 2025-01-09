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

// Main function

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
    

  draw();
  
  canvas.onmousedown = function(evt) {  // zoom out by a factor of 2
  
      let r = canvas.getBoundingClientRect();
      let x = evt.clientX - r.left;
      let y = evt.clientY - r.top;
      
      let old_xmin = mbView.getFloat32(0,true);
      let old_xmax = mbView.getFloat32(4,true);
      let old_ymin = mbView.getFloat32(8,true);
      let old_ymax = mbView.getFloat32(12,true);
      let oldWidth = (old_xmax - old_xmin);
      let newWidth = oldWidth/2;
      let oldHeight = (old_ymax - old_ymin);
      let newHeight = oldHeight/2;
      let pixelWidth = newWidth/800;
      let pixelHeight = newHeight/600;

      let centerX = old_xmin + (x * oldWidth) / 800;
      let centerY = old_ymax - (y * oldHeight) / 600;
      let newXmin,newXmax,newYmin,newYmax;
      newXmin = centerX - x * pixelWidth;
      newYmax = centerY + y * pixelHeight;
      newYmin = newYmax - newHeight;
      newXmax = newXmin + newWidth;
      
     // console.log(old_xmin, old_xmax, old_ymin, old_ymax);
     // console.log(newXmin, newXmax, newYmin, newYmax);
     
     console.log("dx = ", (newXmax - newXmin) / 800);
      
      mbView.setFloat32(0,newXmin,true);
      mbView.setFloat32(4,newXmax,true);
      mbView.setFloat32(8,newYmin,true);
      mbView.setFloat32(12,newYmax,true);
      draw();
  };
  
  function draw() {
      //console.log("\nStart draw");

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
  
  
}

init();
