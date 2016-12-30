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
    to: null
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
    pieces: []
  },

  addMovement: function(from, to, callback) {
    var self = this;
    var movement = new MovementModel({from: from, to: to});
    movement.save(null, {
      id: self.get('id'),
      success: callback
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
    if (this.model.get('id')) {
      this.model.fetch({
        success: function() {
          self.render();
        }
      });
    } else {
      this.model.save(null, {
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
