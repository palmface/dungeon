
var FLOOR = 1;
var PLAYER = 2;
var MONSTER = 4;
var WALL = 8;

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

World.prototype.drawWall = function(ctx, x, y) {
  ctx.fillStyle = "rgba(0, 0, 0, 1.0)";
  ctx.fillRect (x, y, this.tileSize, this.tileSize);
}

World.prototype.drawMonster = function(ctx, x, y) {
  ctx.fillStyle = "rgba(0, 200, 0, 0.5)";
  ctx.fillRect (x, y, this.tileSize, this.tileSize);
}

World.prototype.index = function(x, y) {
  return this.mapWidth * y + x;
}

World.prototype.updateWorld = function(data) {
  if (this.mapWidth*this.mapHeight < data.width*data.height) {
    this.contents = new Array(data.width*data.height);
  }

  this.tileSize = Math.min(this.canvasWidth, this.canvasHeight)/
    Math.max(this.mapWidth, this.mapHeight);
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
  } else if (o.type == "monster") {
    this.contents[i] = MONSTER;
  } else if (o.type == "wall") {
    this.contents[i] = WALL;
  }
}

World.prototype.draw = function(canvas) {
  var ctx = canvas.getContext('2d');

  for (y = 0; y < this.mapHeight; y += 1) {
    for (x = 0; x < this.mapWidth; x += 1) {
      var i = this.index(x,y);
      var drawX = x * this.tileSize;
      var drawY = y * this.tileSize;

      if (this.contents[i] == FLOOR) {
        this.drawFloor(ctx, drawX, drawY);
      } else if (this.contents[i] == WALL) {
        this.drawWall(ctx, drawX, drawY);
      } else if (this.contents[i] == MONSTER) {
        this.drawMonster(ctx, drawX, drawY);
      }
    }
  }

  this.crawler.draw(ctx, this.tileSize);
}

