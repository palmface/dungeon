function Crawler(x, y) {
  this.x = x;
  this.y = y;
  this.size = 10;
}

Crawler.prototype.move = function(direction) {
  if (direction == DOWN) {
    this.y += this.size;
  } else if (direction == UP) {
    this.y -= this.size;
  } else if (direction == LEFT) {
    this.x -= this.size;
  } else {
    this.x += this.size;
  }
}

Crawler.prototype.draw = function(ctx) {
  ctx.fillStyle = "rgba(100, 0, 0, 0.5)";
  ctx.fillRect(this.x, this.y, this.size, this.size);
}

