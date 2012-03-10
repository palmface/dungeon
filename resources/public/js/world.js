var UP = 0;
var DOWN = 1;
var LEFT = 2;
var RIGHT = 3;

function World(width, height, crawler) {
  this.width = width;
  this.height = height;
  this.crawler = new Crawler(5,5);
}

World.prototype.update = function(data) {
  console.log("world update");
  console.log(data);
}

World.prototype.draw = function(canvas) {
  var ctx = canvas.getContext('2d');
  ctx.fillStyle = "rgba(0, 0, 200, 0.5)";
  ctx.fillRect (0, 0, this.width, this.height);

  this.crawler.draw(ctx);
}

