Names.addClass('web.XHR', ['util.EventManager', 'util.Logger'], function (options) {
	this.xhr = null;
	
	try {
        this.xhr = new XMLHttpRequest();
    } catch (e) {
        try {
            this.xhr = new ActiveXObject('MSXML2.XMLHTTP');
        } catch (e1) {
            try {
                this.xhr = new ActiveXObject('Microsoft.XMLHTTP');
            } catch (e2) {
                throw new Error('Can not create AJAX request.');
            }
        }
    }
    this.logger = util.Logger.getLogger({name:'web.XHR'});
    
    this.method = options.method ? options.method : 'GET';
    this.async = options.async !== undefined ? options.async : true;
    this.url = options.url;
    this.data = options.data ? options.data : null;
    
    this.eventManager = new util.EventManager('web.XHR');
    
    if (options.success) this.eventManager.addEvent('success', options.success);
    if (options.failed) this.eventManager.addEvent('failed', options.failed);
    if (options.completed) this.eventManager.addEvent('completed', options.completed);
    
    var self = this;
    if (this.async === true) {
    	this.xhr.onreadystatechange = function () {
    		self.stateChanged.call(self);
    	};
    }
});

Names.depends(['web.XHR'], function () {
	web.XHR.prototype.fireCompleted = function () {
		this.logger.log('Request finished.');
	    var status = this.xhr.status;
	    if (status >= 200 && status < 300) {
	    	this.eventManager.fireEvent('success', [this.xhr, this.url]);
	    } else {
	    	this.eventManager.fireEvent('failed', [this.xhr, this.url]);
	    }
	    this.eventManager.fireEvent('completed', [this.xhr, this.url]);
	}
	
	web.XHR.prototype.stateChanged = function () {
		if (this.xhr.readyState != 4) return;
		this.fireCompleted();
	}
	
	web.XHR.prototype.send = function (url, data) {
		if (url) this.url = url;
		if (!this.url) throw new Error("Need a URL to send a request.");
		
		if (!data) data = this.data;
		if (typeof data == 'object') {
			var s = '';
			for (var n in data) {
				s += n + '=' + data[n] + '&';
			}
			s = s.substring(0, s.length - 1);
			data = s;
		}
		
		this.xhr.open(this.method, this.url, this.async);
		
		if (this.method == 'POST') {
	    	this.xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
	    }
		this.xhr.send(data);
		
		if (this.async === false) {
			this.fireCompleted();
		}
	}
});