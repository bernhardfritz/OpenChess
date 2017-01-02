var $ = global.jQuery = require('jquery');
require('jquery-ui-browserify');
var _ = require('underscore');
var Backbone = require('backbone');

var files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
var ranks = ['1', '2', '3', '4', '5', '6', '7', '8'];
var glyphicons = {
  KING: 'glyphicon-king',
  QUEEN: 'glyphicon-queen',
  ROOK: 'glyphicon-tower',
  BISHOP: 'glyphicon-bishop',
  KNIGHT: 'glyphicon-knight',
  PAWN: 'glyphicon-pawn'
};
var colors = {
  WHITE: 'white',
  BLACK: 'black'
};

var MovementModel = Backbone.Model.extend({
  defaults: {
    from: null,
    to: null,
    token: null
  },

  sync: function(method, model, options) {
    options = options || {};
    var methodToURL = {
        'create': '/boards/' + options.id + '/movement',
        'read': '', // not allowed
        'update': '', // not allowed
        'delete': '' // not allowed
    };
    options.url = methodToURL[method.toLowerCase()];
    return Backbone.sync.apply(this, arguments);
  }
});

var BoardModel = Backbone.Model.extend({
  defaults: {
    id: null,
    pieces: [],
    token: null
  },

  update: function(data, updateView) {
    var pieces = this.get('pieces');
    var fromPiece = _.findWhere(pieces, {position: data.from});
    var fromIndex = pieces.indexOf(fromPiece);
    var toPiece = _.findWhere(pieces, {position: data.to});
    var toIndex = pieces.indexOf(toPiece);
    pieces[fromIndex].position = data.to;
    if (toIndex > -1) {
      pieces.splice(toIndex, 1);
    }
    this.set({pieces: pieces});
    if (updateView) {
      var spanFrom = $('#' + data.from + ' span').detach();
      var tdTo = $('#' + data.to);
      tdTo.empty();
      spanFrom.appendTo(tdTo);
      this.set({token: data.token});
    }
  },

  addMovement: function(from, to, callback) {
    var self = this;
    var movement = new MovementModel({from: from, to: to, token: self.get('token')});
    movement.save(null, {
      id: self.get('id'),
      success: function() {
        self.update({from: from, to: to});
        callback();
      }
    });
  },

  sync: function(method, model, options) {
    options = options || {};
    var methodToURL = {
      'create': '/boards',
      'read': '/boards/' + model.id,
      'update': '', // not allowed
      'delete': '' // not allowed
    };
    options.url = methodToURL[method.toLowerCase()];
    return Backbone.sync.apply(this, arguments);
  }
});

var ChessboardView = Backbone.View.extend({
  initialize: function() {
    var self = this;

    this.model.set({id: $('#chessboard').attr('rel')});

    if (this.model.get('id')) {
      this.model.fetch({
        success: function() {
          self.render();
        }
      });
    }
  },

  render: function() {
    this.model.toJSON().pieces.forEach((piece) => {
      $('#'+ piece.position).html('<span class="glyphicon ' + glyphicons[piece.type] + ' ' + colors[piece.color] + '" />');
    });

    $('table td span').draggable({
      revert: true,
      helper: "clone",
      containment: "document",
      start: function(event, ui) {
        $(this).hide();
        ui.helper.zIndex(1);
      },
      stop: function(event, ui) {
        $(this).show();
      }
    });

    return this;
  }
});

function renderBoardLayout(id) {
  var html = '<table>';
  ranks.reverse().forEach((rank) => {
    html += '<tr>';
    files.forEach((file) => {
      html += '<td id="' + file + rank + '" />';
    });
    html += '</tr>';
  });
  html += '</table>';
  $(id).html(html);
}

$(function() {
  renderBoardLayout('#chessboard');

  var board = new BoardModel();
  var chessboard = new ChessboardView({model: board});
  var ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/ws");
  ws.onopen = function(event) {
    ws.send(JSON.stringify({type: 'HASH', hash: window.location.href.substr(window.location.href.lastIndexOf('/') + 1)}));
  };
  ws.onmessage = function(message) {
    var data = JSON.parse(message.data);
    switch(data.type) {
        case 'MOVEMENT':
          board.update(data, true);
          break;
    }
  };

  $('table td').droppable({
    drop: function(event, ui) {
      var from = ui.draggable.closest('td').attr('id');
      var to = $(this).attr('id');
      board.addMovement(from, to, function() {
        ui.helper.hide();
        $(this).empty();
        var span = ui.draggable.detach();
        span.appendTo($(this));
        span.show();
      }.bind(this, ui));
    }
  });
});
