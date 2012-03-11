var url = 'api/';

function createWorldUpdater(world, canvas) {
  return function(data) {
    world.updateWorld(data);
    clearCanvas(canvas);
    world.draw(canvas);
  }
}

function clearCanvas(canvas) {
  var ctx = canvas.getContext('2d');

  ctx.save();

  ctx.setTransform(1, 0, 0, 1, 0, 0);
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  ctx.restore();
}

function sendAction(action, world, canvas) {
  $.getJSON(url + action, {}, createWorldUpdater(world, canvas));
}

function createKeyDownHandler(world, canvas) {
  return function(e) {
    if (e.keyIdentifier == "Left") {
      sendAction("west", world, canvas);
    } else if (e.keyIdentifier == "Right") {
      sendAction("east", world, canvas);
    } else if (e.keyIdentifier == "Up") {
      sendAction("north", world, canvas);
    } else if (e.keyIdentifier == "Down") {
      sendAction("south", world, canvas);
    }
  }
}

function init() {
  var canvas = document.getElementById('dungeon');
  if (!canvas.getContext) {
    throw("unable to get canvas context");
  }
  var world = new World(canvas.width, canvas.height);
    
  $("#bg-music").get(0).play(); 
  sendAction("", world, canvas);
  sendAction("", world, canvas);
  document.onkeydown = createKeyDownHandler(world, canvas);
}

