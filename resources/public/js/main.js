var url = 'dummy.json';

function createWorldUpdater(world, canvas) {
  return function(data) {
    world.update(data);
    world.redraw(canvas);
  }
}

function createKeyDownHandler(world, canvas) {
  return function(e) {
    if (e.keyIdentifier == "Left") {
      $.getJSON(url, {}, createWorldUpdater(world, canvas));
    } else if (e.keyIdentifier == "Right") {
      $.getJSON(url, {}, createWorldUpdater(world, canvas));
    } else if (e.keyIdentifier == "Up") {
      $.getJSON(url, {}, createWorldUpdater(world, canvas));
    } else if (e.keyIdentifier == "Down") {
      $.getJSON(url, {}, createWorldUpdater(world, canvas));
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

