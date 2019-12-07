(function(window) {
	var AJAX_REQ_TYPE = "POST"
	var AJAX_REQ_DATATYPE = "json";
	var tipBoxTimeout = 0;
	var html = '<div id="[Id]" class="modal fade" role="dialog" aria-labelledby="modalLabel">' + '<div class="modal-dialog modal-sm">' + '<div class="modal-content">' + '<div class="modal-header">'
			+ '<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' + '<h4 class="modal-title" id="modalLabel">[Title]</h4>' + '</div>' + '<div class="modal-body">' + '<p>[Message]</p>'
			+ '</div>' + '<div class="modal-footer">' + '<button type="button" class="btn btn-default cancel" data-dismiss="modal">[BtnCancel]</button>' + '<button type="button" class="btn btn-primary ok" data-dismiss="modal">[BtnOk]</button>' + '</div>' + '</div>' + '</div>'
			+ '</div>';

	var dialogdHtml = '<div id="[Id]" class="modal fade" role="dialog" aria-labelledby="modalLabel">' + '<div class="modal-dialog">' + '<div class="modal-content">' + '<div class="modal-header">'
			+ '<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' + '<h4 class="modal-title" id="modalLabel">[Title]</h4>' + '</div>' + '<div class="modal-body">' + '</div>' + '</div>'
			+ '</div>' + '</div>';

	var reg = new RegExp("\\[([^\\[\\]]*?)\\]", 'igm');
	var generateId = function() {
		var date = new Date();
		return 'mdl' + date.valueOf();
	};

	var init = function(options) {
		options = $.extend({}, {
			title : "操作提示",
			message : "提示内容",
			btnok : "确定",
			btncl : "取消",
			width : 200,
			auto : false
		}, options || {});
		var modalId = generateId();
		var content = html.replace(reg, function(node, key) {
			return {
				Id : modalId,
				Title : options.title,
				Message : options.message,
				BtnOk : options.btnok,
				BtnCancel : options.btncl
			}[key];
		});
		$('body').append(content);
		$('#' + modalId).modal({
			width : options.width,
			backdrop : 'static'
		});
		$('#' + modalId).on('hide.bs.modal', function(e) {
			$('body').find('#' + modalId).remove();
		});
		return modalId;
	};

	var util = {
		tryRun : function(call) {
			try {
				call();
			} catch (e) {
				console.error(e)
			}
		},
		isNumber : function(str) {
			return (str.match(/^\d+$/) != null)
		},
		isDoubleNumber : function(str) {
			return (str.match(/^(-)?\d+(\.\d+)?$/) != null)
		},
		isFormatDate : function(str) {
			return (str.match(/^\d{4}-\d{2}-\d{2}$/) != null)
		},
		requestHashToArray : function(param) {
			if (param.indexOf("#") == 0) {
				param = param.substr(1);
			}
			var result = param.split("#");
			return result
		},
		/***********************************************************************
		 * 格式化金额
		 * 
		 * @param amount
		 *            金额
		 * @param fixed
		 *            小数位数
		 * @returns {String}
		 */
		decimalPlaces : function(amount, fixed, hix) {
			return Number(amount).toFixed(fixed || 2) + (hix || '');
		},
		changeToBusy : function(element, msg) {
			if (!element) {
				element = $("html")
			}
			var width = $(element).width();
			var height = $(element).height();
			var left = $(element).offset().left;
			var top = $(element).offset().top;

			var id = "_____BusyWindow";
			var divHtml = "<div id=\"" + id + "\" style=\"position: absolute; z-index: 10000; font-size: 10pt; font-weight:bold; background-color: rgba(187,187,187,0.3); filter: Alpha(opacity = 40); text-align: center; color: #a29090; overflow: hidden;\">";
			divHtml = divHtml + "<div id=\"timeSpan\" style=\"margin:" + (height * 0.33) + 'px auto;font-size: 1.2rem;">';
			divHtml = divHtml + "<progress>";
			divHtml = divHtml + "<img style='opacity: .3' src = \"" + this.getContextPath() + "/static/images/processing.gif\">";
			divHtml = divHtml + "</progress>";
			divHtml = divHtml + "<div style='padding: 0.5rem;'>" + (msg || '请稍后...') + "</div>";
			divHtml = divHtml + "</div>";
			divHtml = divHtml + "</div>";
			$(element).prepend(divHtml);
			$(element).children("#_____BusyWindow").width(width);
			$(element).children("#_____BusyWindow").height(height);
			$(element).children("#_____BusyWindow").css("left", left);
			$(element).children("#_____BusyWindow").css("top", top);
			$("#" + id).focus();

		},
		changeToArrow : function(element) {
			if (element === false)
				return;

			if (!element) {
				element = $("html")
			}

			if ($(element).children("#_____BusyWindow")) {
				$(element).children("#_____BusyWindow").remove();
			}
		},
		getContextPath : function() {
			var path = window.$cpx || window.cpx || window.ctx;
			if (typeof path === 'string')
				return path;
			path = document.location.pathname;
			var pos = path.indexOf("/", 1);
			return path.substring(0, pos);

		},
		ajax : function(element, data, sucCallback, url, requestType, isAsync) {
			try {
				if (requestType) {
					AJAX_REQ_TYPE = requestType;
				}
			} catch (e) {
			}
			var that = this
			jQuery.ajax({
				type : AJAX_REQ_TYPE,
				url : url,
				cache : false,
				data : data,
				async : isAsync === undefined ? true : isAsync,
				dataType : AJAX_REQ_DATATYPE,
				timeout : 20000, // 超时时间设置，单位毫秒
				beforeSend : function(XMLHttpRequest) {
					that.changeToBusy(element);
				},
				success : function(data) {
					if (!data.HeadInfo || data.HeadInfo == undefined) {
						that.tryRun(function() {
							sucCallback(data)
						});
						return false;
					}

					if (data.HeadInfo.Message != "") {
						that.showTips(data.HeadInfo.Message, false, data.HeadInfo.Code == "0");
					}

					if (data.HeadInfo.Code == "0") {
						that.tryRun(function() {
							sucCallback(data.DataInfo, data);
						});
					}
				},
				complete : function(XMLHttpRequest, textStatus) {
					that.changeToArrow(element);
				},
				error : function(XMLHttpRequest, textStatus) {
					if (XMLHttpRequest.readyState == 4 && XMLHttpRequest.status == 200) {
						var data = XMLHttpRequest.responseText;
						if (Object.prototype.toString.call(data) === "[object String]") {
							if (data.indexOf('HeadInfo') >= 0) {
								data = jQuery.parseJSON(data);
							}
						}
						if (data.HeadInfo.Message != "") {
							that.showTips(data.HeadInfo.Message, false, data.HeadInfo.Code == "0");
						}

						if (data.HeadInfo.Code == "0") {
							that.tryRun(function() {
								sucCallback(data.DataInfo);
							});
						}
					} else if (textStatus === 'timeout') {// 超时,status还有success,error等值的情况
						that.showTips("请求超时!");
					} else {
						that.showTips("服务器连接失败,请稍候再试!");
					}
				}
			});
		},
		ajaxNoMsg : function(element, data, sucCallback, url) {
			var that = this
			jQuery.ajax({
				type : AJAX_REQ_TYPE,
				url : url,
				cache : false,
				data : data,
				async : true,
				dataType : AJAX_REQ_DATATYPE,
				timeout : 10000, // 超时时间设置，单位毫秒
				beforeSend : function(XMLHttpRequest) {
					that.changeToBusy(element);
				},
				success : sucCallback,
				complete : function(XMLHttpRequest, textStatus) {
					that.changeToArrow(element);
				},
				error : function(xmlHttpRequest, textStatus) {
					if (textStatus == 'timeout') {
						// 超时,status还有success,error等值的情况
						that.showTips("请求超时!");
					} else {
						that.showTips("服务器连接失败,请稍候再试!");
					}
				}
			});
		},
		ajaxWithStatus : function(element, data, sucCallback, errCallback, url) {
			var that = this
			var dtype = 'json';
			if (url.indexOf('.do') >= 0) {
				dtype = 'html';
			}
			jQuery.ajax({
				type : AJAX_REQ_TYPE,
				url : url,
				cache : false,
				data : data,
				async : true,
				dataType : dtype,
				timeout : 10000, // 超时时间设置，单位毫秒
				beforeSend : function(XMLHttpRequest) {
					that.changeToBusy(element);
				},
				success : function(data) {
					if (Object.prototype.toString.call(data) === "[object String]") {
						if (data.indexOf('HeadInfo') >= 0) {
							data = jQuery.parseJSON(data);
						}
					}

					if (data.HeadInfo.Message != "") {
						that.showTips(data.HeadInfo.Message, false, data.HeadInfo.Code == "0");
					}

					if (data.HeadInfo.Code == "0") {
						that.tryRun(function() {
							sucCallback(data.DataInfo, data);
						});
					} else {
						that.tryRun(function() {
							errCallback(data);
						});
					}
				},
				complete : function(XMLHttpRequest, textStatus) {
					that.changeToArrow(element);
				},
				error : function(xmlHttpRequest, textStatus) {
					console.log(xmlHttpRequest, textStatus)
					if (textStatus == 'timeout') {
						// 超时,status还有success,error等值的情况
						that.showTips("请求超时!");
					} else {
						that.showTips("服务器连接失败,请稍候再试!");
					}
					that.tryRun(function() {
						errCallback();
					});
				}
			});
		},
		submit : function(form, sucCallback, errCallback) {
			var url = $(form).attr('action');
			var param = $(form).serialize();
			this.ajaxWithStatus($('html'), param, sucCallback, errCallback, url);
		},
		/***********************************************************************
		 * 显示悬浮提示
		 * 
		 * @param text
		 *            提示文本
		 * @param time
		 *            提示时长
		 * @param status
		 *            状态 0 错误 1 正常
		 * @returns
		 */
		showTips : function(text, time, status, callback) {
			// 当有父级页面时候,使用父级页面窗口调用对象避免本窗口关闭时 tips 未销毁
			if (top && top != window && top.util) {
				top.util.showTips(text, time, status, callback);
				return;
			}
			if (!text || (typeof text == 'string' && !text.trim())) {
				return;
			}

			time = time ? time : 2000;
			status = status ? status : 0;
			var htmlCon;
			var color, border = '0';
			if (status == 'success' || status === 1 || status === true) {
				color = '#4AAF33';
			} else if (status == 'warn') {
				color = '#eaa000';
			} else {
				color = '#dd4b39';
			}

			clearTimeout(tipBoxTimeout);
			$(top.document).find('#______sys_tip_box').trigger('tip.remove').remove();

			htmlCon = '<div id="______sys_tip_box" class="tipsBox" style="width:220px;padding:10px;background-color:{color};border-radius:4px;-webkit-border-radius: 4px;-moz-border-radius: 4px;color:#FFF;box-shadow:0 0 3px #ddd inset;-webkit-box-shadow: 0 0 3px #ddd inset;border:{border}px solid #eaa000; text-align:center;position:fixed;top:5px;left:50%;z-index:999999;margin-left:-120px;">{txt}</div>'
			var domBox = $(htmlCon.format({
				id : '',
				color : color,
				txt : text,
				border : border
			}));
			$(domBox).on('tip.remove', callback || function() {
			});
			$(top.document).find('body').prepend(domBox);
			tipBoxTimeout = top.setTimeout(function() {
				$(top.document).find('#______sys_tip_box').trigger('tip.remove').remove();
			}, time);
		},
		resetDateBox : function(id, param) {
			if (!param) {
				param = {};
			}
			var width = param.width ? param.width : '100%';
			var height = param.height ? param.height : '34px';
			var fontsize = param.fontsize ? param.fontsize : '14px';
			var color = param.color ? param.color : '#555';
			var bgcolor = param.bgcolor ? param.bgcolor : '#fff';
			var padding = param.padding ? param.padding : '6px 12px';
			var icontop = param.icontop ? param.icontop : '6px';
			$('#' + id).next().css('width', width);
			$('#' + id).next().css('height', height);
			$('#' + id).parent('td').attr('id', 'logistextbox');
			$('#' + id).next().addClass('textbox-logis');
			$('#' + id).next().find('.textbox-text').css('width', '100%');
			$('#' + id).next().find('.textbox-text').css('font-size', fontsize);
			$('#' + id).next().find('.textbox-text').css('color', color);
			$('#' + id).next().find('.textbox-text').css('background-color', bgcolor);
			$('#' + id).next().find('.textbox-text').css('padding', padding);
			$('#' + id).next().data('focuseClass', 'textbox-focused-logis');
			$('#' + id).next().find('.textbox-icon').css('margin-top', icontop);
		},
		showImg : function(imgUrl) {
			var _random = Math.ceil(Math.random() * 100000);
			var _id = "showID-" + _random;
			var _html = "<div title='点击任意位置关闭大图' id='" + _id + "' style='position:fixed;top:0;left:0;right:0;bottom:0;z-index:999999999999;cursor: pointer; background:rgba(0,0,0,.5)'>"
					+ "<img style='position: absolute;top:0;left:0;right:0;bottom:0;margin:auto;max-height:90%;width:100%;' src='" + imgUrl + "' />" + "</div>";
			$("body").append(_html);
			$("#" + _id).on("click", function() {
				var _bg = $(this);
				_bg.remove();
			});
		},
		valids : function(list) {
			for (var i = 0; i < list.length; i++) {
				var temp = list[i];
				var str = temp.id ? ("#" + temp.id) : (temp.name ? ("input[name=" + temp.name + "]") : temp.selector);
				console.debug('create valid datebox:', temp);
				$(str).validatebox({
					required : temp.required,
					missingMessage : temp.requiredMessage,
					validType : temp.vType
				});
			}
		},
		dialog : {
			alert : function(options) {
				if (typeof options == 'string') {
					options = {
						message : options
					};
				}
				var id = init(options);
				var modal = $('#' + id);
				modal.find('.ok').removeClass('btn-success').addClass('btn-primary');
				modal.find('.cancel').hide();

				return {
					id : id,
					on : function(callback) {
						if (callback && callback instanceof Function) {
							modal.find('.ok').click(function() {
								callback(true);
							});
						}
					},
					hide : function(callback) {
						if (callback && callback instanceof Function) {
							modal.on('hide.bs.modal', function(e) {
								callback(e);
							});
						}
					}
				};
			},
			confirm : function(options) {
				var id = init(options);
				var modal = $('#' + id);
				modal.find('.ok').removeClass('btn-primary').addClass('btn-success');
				modal.find('.cancel').show();
				return {
					id : id,
					on : function(callback) {
						if (callback && callback instanceof Function) {
							modal.find('.ok').click(function() {
								callback(true);
							});
							modal.find('.cancel,.close').click(function() {
								callback(false);
							});
						}
					},
					hide : function(callback) {
						if (callback && callback instanceof Function) {
							modal.on('hide.bs.modal', function(e) {
								callback(e);
							});
						}
					}
				};
			},
			dialog : function(options) {
				options = $.extend({}, {
					title : 'title',
					url : '',
					width : 800,
					height : 550,
					onReady : function() {
					},
					onShown : function(e) {
					}
				}, options || {});
				var modalId = generateId();

				var content = dialogdHtml.replace(reg, function(node, key) {
					return {
						Id : modalId,
						Title : options.title
					}[key];
				});
				$('body').append(content);
				var target = $('#' + modalId);
				target.find('.modal-body').load(options.url);
				if (options.onReady())
					options.onReady.call(target);
				target.modal();
				target.on('shown.bs.modal', function(e) {
					if (options.onReady(e))
						options.onReady.call(target, e);
				});
				target.on('hide.bs.modal', function(e) {
					$('body').find(target).remove();
				});
			}
		},
		convertDate : function(dateObj) {
			if (typeof obj != 'object') {
				dateObj = dateObj.replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '').replace(/(-)/g, '/')
				if (dateObj.indexOf(".") > 0)
					dateObj = dateObj.slice(0, dateObj.indexOf("."))
				return new Date(dateObj)
			} else if (this.isDate(dateObj)) {
				return dateObj;
			}
		}
	};

	Date.prototype.format = function(fmt) {
		var q = Math.floor((this.getMonth() + 3) / 3);
		var o = {
			"M+" : this.getMonth() + 1, // 月份
			"d+" : this.getDate(), // 日
			"h+" : this.getHours(), // 小时
			"m+" : this.getMinutes(), // 分
			"s+" : this.getSeconds(), // 秒
			"q+" : q, // 季度
			// 毫秒
			"S" : this.getMilliseconds()
		};

		if (/(y+)/.test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		}
		for ( var k in o) {
			if (new RegExp("(" + k + ")").test(fmt)) {
				fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length))));
			}
		}
		return fmt;
	}

	window.Overload = function(fn_objs) {
		var is_match = function(x, y) {
			if (x == y)
				return true;
			if (x.indexOf("*") == -1)
				return false;

			var x_arr = x.split(","), y_arr = y.split(",");
			if (x_arr.length != y_arr.length)
				return false;

			while (x_arr.length) {
				var x_first = x_arr.shift(), y_first = y_arr.shift();
				if (x_first != "*" && x_first != y_first)
					return false;
			}
			return true;
		};
		var ret = function() {
			var args = arguments, args_len = args.length, args_types = [], args_type, fn_objs = args.callee._fn_objs, match_fn = function() {
			};

			for (var i = 0; i < args_len; i++) {
				var type = typeof args[i];
				type == "object" && (args[i].length > -1) && (type = "array");
				args_types.push(type);
			}
			args_type = args_types.join(",");
			for ( var k in fn_objs) {
				if (is_match(k, args_type)) {
					match_fn = fn_objs[k];
					break;
				}
			}
			return match_fn.apply(this, args);
		};
		ret._fn_objs = fn_objs;
		return ret;
	};

	String.prototype.format = Overload({
		"array" : function(params) {
			var reg = /{(\d+)}/gm;
			return this.replace(reg, function(match, name) {
				return params[~~name];
			});
		},
		"object" : function(param) {
			var reg = /{([^{}]+)}/gm;
			return this.replace(reg, function(match, name) {
				return param[name];
			});
		}
	});

	Number.prototype.preFixed = function(len) {
		return (Array(len).join('0') + this).slice(-len);
	};
	// 模态框关闭事件
	$(document).on('hide.bs.modal', '.modal.fade', function() {
		if ($(this).find('form').length > 0) {
			$(this).find('form').validate().resetForm();
			$(this).find('form :input').each($.fn.Form.defaults.validateSuccess)
		}
	});
	window.util = util;
})(window)