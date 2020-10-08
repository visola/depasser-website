Names.depends([ 'web.StateManager', 'web.XHR', 'JSON' ], function() {
	var xhr = new web.XHR({
		method : 'POST',
		url : ROOT + 'tracker.json'
	});
	
	web.StateManager.addEvent('stateChanged', sendData);
	
	function createTrackObject() {
		return {
			d : new Date(),
			a : navigator.userAgent,
			l : navigator.language,
			s : web.StateManager.getState(),
			sc : {
				h : screen.availHeight,
				w : screen.availWidth,
				cd : screen.colorDepth
			}
		};
	}
	
	function sendData () {
		xhr.send(null, {data: JSON.stringify(createTrackObject())});
	}
	
	sendData();
});