function assert(expr, msg) {
  if (expr === false) {
    console.log(msg);
  }
}

function draw(ctx) {
  ctx.fillStyle = "rgb(200,0,0)";
  ctx.fillRect (10, 10, 55, 50);

  ctx.fillStyle = "rgba(0, 0, 200, 0.5)";
  ctx.fillRect (30, 30, 55, 50);
}

function createKeyDownHandler(world, canvas) {
  return function(e) {
    if (e.keyIdentifier == "Left") {
      world.crawler.move(LEFT);
      world.redraw(canvas);
    } else if (e.keyIdentifier == "Right") {
      world.crawler.move(RIGHT);
      world.redraw(canvas);
    } else if (e.keyIdentifier == "Up") {
      world.crawler.move(UP);
      world.redraw(canvas);
    } else if (e.keyIdentifier == "Down") {
      world.crawler.move(DOWN);
      world.redraw(canvas);
    }
  }
}

function init() {
  var canvas = document.getElementById('dungeon');
  if (!canvas.getContext) {
    throw("unable to get canvas context");
  }
  var crawler = new Crawler(5,5);
  var world = new World(canvas.width, canvas.height, crawler);
  document.onkeydown = createKeyDownHandler(world, canvas);
}

