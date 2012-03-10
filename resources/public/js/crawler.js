function Crawler(x, y) {
  this.x = x;
  this.y = y;
}

Crawler.prototype.setPosition = function(x, y) {
  this.x = x;
  this.y = y;
}

Crawler.prototype.draw = function(ctx, size) {
  ctx.fillStyle = "rgba(200, 0, 0, 0.5)";
  ctx.fillRect(this.x*size, this.y*size, size, size);
}

