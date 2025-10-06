const canvas = document.getElementById("frameCanvas") as HTMLCanvasElement;
const ctx = canvas.getContext("2d")!;
const statsEl = document.getElementById("stats")!;

// Sample frame (you can replace with Base64 from Android)
const sampleImage = new Image();
sampleImage.src = "sample_frame.png"; // save a processed frame from your app
sampleImage.onload = () => {
    canvas.width = sampleImage.width;
    canvas.height = sampleImage.height;
    ctx.drawImage(sampleImage, 0, 0);
    statsEl.textContent = `Resolution: ${sampleImage.width}x${sampleImage.height} | FPS: 0`;
};

// Optional: simulate frame updates
let lastTime = performance.now();
function updateFrame() {
    const now = performance.now();
    const fps = Math.round(1000 / (now - lastTime));
    lastTime = now;

    // redraw sample image (replace with live frame from WebSocket later)
    ctx.drawImage(sampleImage, 0, 0);
    statsEl.textContent = `Resolution: ${sampleImage.width}x${sampleImage.height} | FPS: ${fps}`;

    requestAnimationFrame(updateFrame);
}

// Start simulated loop
updateFrame();
