$(function(){
    if ((document.location.host.indexOf('.dev') > -1) || (document.location.host.indexOf('modernui') > -1) ) {
        $("<script/>").attr('src', 'lib/metro/metro-loader.js').appendTo($('head'));
    } else {
        $("<script/>").attr('src', 'lib/metro.min.js').appendTo($('head'));
        $("<script/>").attr('src', 'lib/RecordRTC.min.js').appendTo($('head'));
    }
});