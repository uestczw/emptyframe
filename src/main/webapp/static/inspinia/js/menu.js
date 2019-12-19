function SmoothlyMenu() {
	if (!$('body').hasClass('mini-navbar') || $('body').hasClass('body-small')) {
		// Hide menu in order to smoothly turn on when maximize menu
		$('#side-menu').hide();
		// For smoothly turn on menu
		setTimeout(function() {
			$('#side-menu').fadeIn(400);
		}, 200);
	} else if ($('body').hasClass('fixed-sidebar')) {
		$('#side-menu').hide();
		setTimeout(function() {
			$('#side-menu').fadeIn(400);
		}, 100);
	} else {
		// Remove all inline style from jquery fadeIn function to reset menu
		// state
		$('#side-menu').removeAttr('style');
	}
}
$(document).ready(function() {
	if ($(this).width() < 769) {
		$('body').addClass('body-small')
	} else {
		$('body').removeClass('body-small')
	}
	createMenu(MENU_DATA_ZENITOO);
	$('#side-menu').metisMenu();
	$('.navbar-minimalize').on('click', function() {
		$("body").toggleClass("mini-navbar");
		SmoothlyMenu();

	});
});
function getUrlRelativePath() {
	var url = document.location.toString();
	url = url.replace('//', '/');
	url = url.replace('\\\\', '\\');
	return url;
}
function getRootPath_web() {
	// 获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
	var curWwwPath = window.document.location.href;
	// 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	// 获取主机地址，如： http://localhost:8083
	// var localhostPaht = curWwwPath.substring(0, pos);
	// 获取带"/"的项目名，如：/uimcardprj
	var projectName = pathName
			.substring(0, pathName.substr(1).indexOf('/') + 1);
	return projectName;
}
var menu_one_mb = '<li class="#[isactive]"><a href="#[dz]"><i class="fa #[cd_tb]"></i> <span class="nav-label">#[mc]</span>#[hassec]</a>#[seccon]</li>';
function createMenu(data) {
	var path = getRootPath_web();
	var activeUrl = getUrlRelativePath();
	for (var i = 0; i < data.length; i++) {
		var cd = data[i];
		var tmpone = menu_one_mb;
		tmpone = tmpone.replace('#[mc]', cd.cd_mc);
		tmpone = tmpone.replace('#[cd_tb]', cd.cd_tb);
		if (cd.cd_dz != null && cd.cd_dz != '') {
			if (cd.cd_dz.indexOf(path) >= 0) {
				path = '';
			}
			tmpone = tmpone.replace('#[dz]', path + cd.cd_dz);
			if (activeUrl.indexOf(cd.cd_dz) >= 0) {
				tmpone = tmpone.replace('#[isactive]', 'active');
			} else {
				tmpone = tmpone.replace('#[isactive]', '');
			}
		} else {
			tmpone = tmpone.replace('#[dz]', '#');
		}

		if (cd.children && cd.children.length > 0) {
			var tmpsec = '<ul class="nav nav-second-level collapse">';
			for (var j = 0; j < cd.children.length; j++) {
				var cdsec = cd.children[j];
				if (cd.cd_dz.indexOf(path) >= 0) {
					path = '';
				}
				tmpsec += '<li class="#[isactive]"><a href="' + path
						+ cdsec.cd_dz + '">' + cdsec.cd_mc + '</a></li>';
				if (activeUrl.indexOf(cdsec.cd_dz) >= 0) {
					tmpsec = tmpsec.replace('#[isactive]', 'active');
					tmpone = tmpone.replace('#[isactive]', 'active');
				} else {
					tmpsec = tmpsec.replace('#[isactive]', '');
				}
			}
			tmpsec += '<ul>';
			tmpone = tmpone.replace('#[hassec]',
					'<span class="fa arrow"></span>');
			tmpone = tmpone.replace('#[seccon]', tmpsec);
		} else {
			tmpone = tmpone.replace('#[hassec]', '');
			tmpone = tmpone.replace('#[seccon]', '');
		}
		$('#side-menu').append(tmpone);
	}
}