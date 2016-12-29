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

var PieceModel = Backbone.Model.extend({
  defaults: {
    type: null,
    color: null,
    position: null
  }
});

var PieceCollection = Backbone.Collection.extend({
  url: '/boards/1',
  model: PieceModel,

  parse: function(data) {
    return data.pieces;
  }
});

var ChessboardView = Backbone.View.extend({
  initialize: function() {
    var self = this;
    this.collection.fetch({
      success: function() {
        self.render();
      }
    });
  },

  render: function() {
    this.collection.each((model) => {
      var piece = model.toJSON();
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

  $('table td').droppable({
    drop: function(event, ui) {
        ui.helper.hide();
        var span = ui.draggable.detach();
        span.appendTo($(this));
        span.show();
    }
  });

  var pieceCollection = new PieceCollection();
  var chessboardView = new ChessboardView({collection: pieceCollection});
});
