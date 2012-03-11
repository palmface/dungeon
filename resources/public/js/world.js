
var FLOOR = 1;
var PLAYER = 2;

function World(canvasWidth, canvasHeight) {
  this.canvasWidth = canvasWidth;
  this.canvasHeight = canvasHeight;
  this.mapWidth = 1;
  this.mapHeight = 1;
  this.tileSize = 1;
  this.crawler = new Crawler(0,0);
  this.contents = new Array(this.mapWidth*this.mapHeight);
}

World.prototype.drawFloor = function(ctx, x, y) {
  ctx.fillStyle = "rgba(0, 0, 150, 0.5)";
  ctx.fillRect (x, y, this.tileSize, this.tileSize);
}

World.prototype.index = function(x, y) {
  return this.width * y + x;
}

World.prototype.updateWorld = function(data) {
  if (this.mapWidth*this.mapHeight < data.width*data.height) {
    this.contents = new Array(data.width*data.height);
  }

  this.tileSize = Math.min(this.canvasWidth, this.canvasHeight)/Math.max(this.mapWidth, this.mapHeight);
  this.mapWidth = data.width;
  this.mapHeight = data.height;
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

  for (y = 0; y < this.mapHeight*this.tileSize; y += this.tileSize) {
    for (x = 0; x < this.mapWidth*this.tileSize; x += this.tileSize) {
      this.drawFloor(ctx, x, y);
    }
  }

  this.crawler.draw(ctx, this.tileSize);
}

