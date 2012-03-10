var UP = 0;
var DOWN = 1;
var LEFT = 2;
var RIGHT = 3;

function World(width, height, crawler) {
  this.width = width;
  this.height = height;
  this.crawler = new Crawler(5,5);
}

World.prototype.clearCanvas = function(canvas) {
  var ctx = canvas.getContext('2d');

  ctx.save();

  ctx.setTransform(1, 0, 0, 1, 0, 0);
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  ctx.restore();
}

World.prototype.redraw = function(canvas) {
  var ctx = canvas.getContext('2d');
  this.clearCanvas(canvas);
  this.draw(ctx);
}

World.prototype.draw = function(ctx) {
  ctx.fillStyle = "rgba(0, 0, 200, 0.5)";
  ctx.fillRect (0, 0, this.width, this.height);

  this.crawler.draw(ctx);
}

