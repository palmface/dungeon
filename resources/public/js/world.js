
var FLOOR = 1;
var PLAYER = 2;

function World() {
  this.width = 10;
  this.height = 10;
  this.tileSize = 50;
  this.crawler = new Crawler(5,5);
  this.contents = new Array(100);
}

World.prototype.drawFloor = function(ctx, x, y) {
  ctx.fillStyle = "rgba(0, 0, 150, 0.5)";
  ctx.fillRect (x, y, this.tileSize, this.tileSize);
}

World.prototype.index = function(x, y) {
  return this.width * y + x;
}

World.prototype.updateWorld = function(data) {
  if (this.width*this.height < data.width*data.height) {
    this.contents = new Array(data.width*data.height);
  }

  this.width = data.width;
  this.height = data.height;
  for (i in data.contents)
    this.updateObject(data.contents[i]);
}

World.prototype.updateObject = function(o) {
  var i = this.index(o.x, o.y);
  if (o.type == "floor") {
    this.contents[i] = FLOOR;
  } else if (o.type == "player") {
    this.contents[i] = PLAYER;
    this.crawler.setPosition(o.x, o.y);
  }
}

World.prototype.draw = function(canvas) {
  var ctx = canvas.getContext('2d');

  for (y = 0; y < this.height*this.tileSize; y += this.tileSize) {
    for (x = 0; x < this.width*this.tileSize; x += this.tileSize) {
      this.drawFloor(ctx, x, y);
    }
  }

  this.crawler.draw(ctx, this.tileSize);
}

