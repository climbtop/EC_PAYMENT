var	trendyCall = function(o) {
		var	action,
			list,
			itemNum;
		//console.log(o);
		if (!o) return null;
		if (typeof o === 'object') {
			if ('command' in o) {
				if (o.command.indexOf('userMessage') == 0) {
					te$.business.login.showUserMessage(o);
				}
				if (o.command.indexOf('addCart') == 0) {
					te$.business.cart.afterAdd(o);
				}
				if (o.command.indexOf('getCartInfo') == 0) {
					te$.business.cart.updateCartNum();
				}
				if (o.command.indexOf('deleteCartItem') == 0) {
					if ('mobile' in te$) te$.mobile.cart.afterRemove(o)
					else te$.business.cart.afterRemove(o);
				}
				if (o.command.indexOf('modifyCart') == 0) {
					if ('mobile' in te$) te$.mobile.cart.afterModify(o)
					else te$.business.cart.afterModify(o);
				}
				if (o.command.indexOf('cancelOrder') == 0) {
					te$.business.cart.afterCancelOrder(o);
				}
				if (o.command.indexOf('removeFavorite') == 0) {
					if ('mobile' in te$) te$.business.favorite.afterRemove(o)
					else te$.mobile.favorite.afterRemove(o);
				}
				if (o.command.indexOf('addFavorite') == 0) {
					te$.business.favorite.afterAdd(o);
				}
				if (o.command.indexOf('getProInfo') == 0) {
					te$.business.stock.reset(o);
				}
				if (o.command.indexOf('myTopOrder') == 0) {
					te$.business.login.showMyNewOrder(o);
				}
				if (o.command.indexOf('showSpu') == 0) {
					te$.business.cart.rechooseShow(o);
				}
				if (o.command.indexOf('removeMessage') == 0) {
					te$.message.removeItem(o);
				}
				if (o.command.indexOf('userScore') == 0) {
					if ('mobile' in te$) te$.mobile.showUserScore(o)
					else te$.business.login.showUserScore(o);
				}
				if (o.command.indexOf('reflashCart') == 0) {
					if (window.location.href.indexOf('cart.do') >= 0) {
						if (o.code == -1) {
							te$.ui.msgBox.show('操作失败，请稍候重试', [
								['确定', function(){
									window.location.reload();
								}]
							]);
						} 
						else window.location.reload();
					}
				}
				if (o.command.indexOf('pointExchange') == 0) {
					if (o.code == 1) {
						te$.ui.msgBox.show('兑换成功，请到我的优惠券查看兑换详情');
					}
					else {
						if (o.message) te$.ui.msgBox.show(o.message)
						else te$.ui.msgBox.show('兑换未成功，可能因为积分不足');
					}
				}
				if (o.command.indexOf('couponVerify') == 0) {
					if (window.location.href.indexOf('confirm.do') >= 0) {
						if (o.code == 1) {
							if (o.content.length == 1) {
								if ('mobile' in te$)  te$.mobile.cart.useCouponCode(o.content[0].couponCode)
								else te$.business.cart.useCouponCode(o.content[0].couponCode);
								return;
							}
						}
					}
					te$.ui.msgBox.show('优惠码未能通过有效性验证，请检查是否输入错误。');					
				}
				if (o.command.indexOf('getNewInItems') == 0) {
					te$.business.login.showNewInItems(o);
				}
				if (o.command.indexOf('userCouponNum') == 0) {
					te$.business.login.showUserCouponNum(o);
				}
				if (o.command.indexOf('userVipStatus') == 0) {
					if ('mobile' in te$) te$.mobile.showVipStatus(o);
					else te$.business.login.showVipStatus(o);
				}
			} else return o;
		} else return null;
	},

	/*document.getElementById替代*/	
	t$ = function(b) {
		var a = typeof b == "string" ? document.getElementById(b) : b;
		if (a != null) {
			return a;
		}
		return null;	
	},

	showInfo = function(o) {
		var cValue = 'false^^^^G^^';
		if (typeof o == 'object') {
			if (o.isLogon == 'true') {
				te$.business.user.userName = o.userLoginId;
				te$.business.user.userType = 'A';
				te$.business.user.logonId = o.userId;
				te$.business.user.userLevel = 'B';
				te$.business.user.userId = o.userId;
				te$.business.user.hasLogin = true;
				te$.business.user.shortName = o.userLoginId.length > 7 ? o.userLoginId.substr(0, 6) + '..' : o.userLoginId;
				te$.business.login.loginTime = 0;
				cValue = 'true^' + te$.business.user.logonId + '^' + te$.business.user.userName + '^' + te$.business.user.userLevel + '^' + te$.business.user.userType + '^' + te$.business.user.logonId + '^' + te$.business.user.shortName;
				te$.cookie('client_info', cValue, {'domain':te$.domainName, 'path':'/'});
				te$.cookie('sui', o.userId, {'domain':te$.domainName, 'path':'/'});
				//te$.cookie('trendy_uid', data.o['userId'], {'domain':te$.domainName, 'path':'/'});
			}
			else {
				te$.cookie('client_info', cValue, {'domain':te$.domainName, 'path':'/'});
				//te$.business.login.clearLogin();
			}
		}
		te$.business.login.showRelateInfo();
		te$.business.cart.fill(true);
	},

	te$ = te$ || {};

	te$.stringToJson = function(string) {
		if (JSON) {
			return JSON.parse(string);
		}
		else {
			return (new Function('return ' + string))();
		}
		
	};

	te$.regVerify = {
		/*/^13[0-9]{9}$|14[0-9]{9}|15[0-9]{9}$|18[0-9]{9}$/,
		/^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
		/^(\w+:\/\/)?\w+(\.\w+)+.*$/*/
		'loginName' : {'reg':/(^([-_A-Za-z0-9\.]+)@([-_A-Za-z0-9]+\.)+[A-Za-z0-9]{2,3}$)|(^1[3|4|5|8|7][0-9]\d{8,8}$)/, 'tip':'请输入正确的邮箱或者手机号码'},
		'passwordLength' : {'reg':/^\w{6,16}$|^\w{32}$/, 'tip':'密码需6~16位'},
		'password' : {'reg':/^\w{6,16}$|^\w{32}$/, 'tip':'密码需6~16位'},
		'kaptcha' : {'reg':/^[a-zA-Z0-9]{4}$/, 'tip':'请填写4位验证码'},
		'notNull' : {'reg':/[\w\W]+/, 'tip':'不能为空'},
		'name' : {'reg':/[\w\W]+/, 'tip':'请填写姓名'},
		'address' : {'reg':/[\w\W]+/, 'tip':'请输入具体地址'},
		'city' : {'reg':/[\w\W]+/, 'tip':'请选择省市区县'},
		'email' : {'reg':/^([-_A-Za-z0-9\.]+)@([-_A-Za-z0-9]+\.)+[A-Za-z0-9]{2,3}$/, 'tip':'请输入正确的邮箱地址'},
		'url' : {'reg':/^((http:[/][/])?\w+([.]\w+|[/]\w*)*)?$/, 'tip':'请输入正确的url地址'},
		'english' : {'reg':/^[A-Za-z]+$/, 'tip':'只允许大小写英文'},
		'chinese' : {'reg':/^[\u4e00-\u9fa5]+$/, 'tip':'只允许中文字符'},
		'number' : {'reg':/^[0-9]+$/, 'tip':'只允许数字'},
		'zip' : {'reg':/^[0-9]{6}$/, 'tip':'请填写6位邮政编码'},
		'mobile' : {'reg':/^1[3|4|5|8|7][0-9]\d{8,8}$/, 'tip':'手机号码格式不正确'},
		'phone' : {'reg':/^0([1-9]{2,3}-)?[0-9]{7,8}$/, 'tip':'座机号码格式不正确'},
		'date' : {'reg':/^(\d+)-(\d{1,2})-(\d{1,2})$/, 'tip':'请输入正确的日期格式'},
		'shortTime' : {'reg':/^(\d{1,2}):(\d{1,2})$/, 'tip':'请输入正确的时间格式'},
		'longTime' : {'reg':/^(\d{1,2}):(\d{1,2}):(\d{1,2})$/, 'tip':'请输入正确的时间格式'},
		'dateTime' : {'reg':/^(\d+)-(\d{1,2})-(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/, 'tip':'请输入正确时间的日期格式'}
	};

	te$.orderStatus = {
		'APPROVED' : '已审核',
		'PRINTED' : '已打单',
		'PICKED' : '已拣货',
		'PACKAGED' : '已打包',
		'SENT' : '已发货',
		'SENT_PART' : '部分发货',
		'SENT_FAIL' : '发货失败',
		'FINISHED' : '已送达',
		'APPLY_CANCEL' : '申请取消',
		'CANCELLED' : '已取消',
		'APPLY_REFUND' : '申请退款',
		'REFUNDING' : '退款中',
		'REFUNDED' : '已退款',
		'LACK' : '缺货',
		'WAIT_PAY' : '未支付',
		'PAYED' : '已支付',
		'APPROVED_SUSPEND' : '审核挂起',
		'EDIT' : '正在处理',
		'SUSPEND' : '挂起',
		'OVERSOLD' : '超卖'
	};

	te$.checkTest = function() {
		var h = window.location.host.split('.');
		if (h[0] == 'test') {
			return true;
		}
		else {
			return false;
		}
	};

	te$.isWWW = function() {
		var h = window.location.host.split('.');
		if (h[0] == 'www') {
			return true;
		}
		else {
			return false;
		}
	};

	te$.storeId = (function(){
		if (te$.checkTest()) {
			return ({'ochirly':2, 'fiveplus':5, 'trendiano':6});
		}
		else {
			return ({'ochirly':1, 'fiveplus':2, 'trendiano':6});	
		}
	})();

	te$.newInCatalogId = (function(){
		if (te$.checkTest()) return ({'ochirly' : 3, 'fiveplus' : 21, 'trendiano' : 206})
		else return ({'ochirly' : 23, 'fiveplus' : 21, 'trendiano' : 205});
	})();	

	te$.getCurrHost = function(domain, protocol) {
		var	z = window.location.host.split(':'),
			h = z[0].split('.'),
			d = domain || '',
			base = '',
			start = 1,
			testSite = '',
			pro = (protocol ? protocol : 'http') + '://',
			port = z.length == 2 ? ':' + z[1] : '';
		if (te$.checkTest()) {
			start = 2;
			testSite = 'test.';
		}

		for (var i = start; i < h.length; i++) {
			base += '.' + h[i];
		}

		if (domain == 'sso') base = '.lpmas.com';
		if (domain == 'img2') {
			if (te$.checkTest()) return '//test.static.t-e-shop.com/trendy/global/v1'
			else {
				testSite = '';
				pro = '//';
				if (window.location.protocol == 'https:') d = 'img2s'
				else d = 'img2';
			}
		}
		return pro + testSite + d + base + port; 	
	};

	te$.getCurrBrand = function() {
		var curr = null;
		var h = window.location.host.toLowerCase();
		if (h.indexOf('fiveplus.com') >= 0) curr = 'fiveplus';
		if (h.indexOf('ochirly.com') >= 0) curr = 'ochirly';
		if (h.indexOf('trendiano.com') >= 0) curr = 'trendiano';
		return curr;
	};

	te$.getBrandName = function() {
		var curr = '';
		var h = window.location.host.toLowerCase();
		if (h.indexOf('fiveplus.com') >= 0) curr = 'Five Plus';
		if (h.indexOf('ochirly.com') >= 0) curr = 'ochirly';
		if (h.indexOf('trendiano.com') >= 0) curr = 'TRENDIANO';
		return curr;
	};

	te$.cookie = function(name, value, options) {
		if (typeof value != 'undefined') { // name and value given, set cookie
			options = options || {};
			if (value === null) {
				value = '';
				options.expires = -1;
			}
			var expires = '';
			if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
				var date;
				if (typeof options.expires == 'number') {
					date = new Date();
					date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
				} else {
		    		date = options.expires;
				}
				expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
			}
			// CAUTION: Needed to parenthesize options.path and options.domain
			// in the following expressions, otherwise they evaluate to undefined
			// in the packed version for some reason...
			var path = options.path ? '; path=' + (options.path) : '';
			var domain = options.domain ? '; domain=' + (options.domain) : '';
			var secure = options.secure ? '; secure' : '';
			document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
		} else { // only name given, get cookie
			var cookieValue = null;
			if (document.cookie && document.cookie != '') {
				var cookies = document.cookie.split(';');
				for (var i = 0; i < cookies.length; i++) {
		    		var cookie = jQuery.trim(cookies[i]);
		   			 // Does this cookie string begin with the name we want?
					if (cookie.substring(0, name.length + 1) == (name + '=')) {
		                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
		                break;
		            }
		        }
		    }
		    return cookieValue;
		}
	};

	te$.trendySite = {
		'www' : te$.getCurrHost('www'),
		'my' :  te$.getCurrHost('my'),
		'passport' : te$.checkTest() ? te$.getCurrHost('passport', 'http') : te$.getCurrHost('passport', 'http'),
		'httpsPassport' : te$.getCurrHost('passport', 'http'),
		'sso' : (function(){
			var result;
			if (te$.checkTest()) {
				if (window.location.protocol == 'https:') result = te$.getCurrHost('sso', 'http')
				else result = te$.getCurrHost('sso', 'http');
			}
			else result = te$.getCurrHost('sso', 'http');
			return result;
		})(),
		'httpsSso' : te$.getCurrHost('sso', 'http'),
		'portal' : 'http://test02.teadmin.net:9080',
		'act' : te$.getCurrHost('act'),
		'img2' : te$.getCurrHost('img2')
	};

	te$.isCheckin = function() {
		var	url = window.location.href,
			checkinList = [
				'UserAccountLogin.do',
				'UserAccountRegister.do',
				'UserPasswordRegain.do',
				'UserPasswordRegain.do'
			],
			result = false;
		
		for (var i = 0, l = checkinList.length; i < l; i++) {
			if (url.indexOf(checkinList[i]) >= 0) {
				result = true;
			}
		}

		return result;
	};

	te$.getUrl = function(type) {
		var result = '';
		switch (type) {
			case 'userScore':
				result = te$.trendySite.passport + '/user/UserProfileListJson.do';
				break;
			case 'userMessage':
				result = te$.trendySite.act + '/applets/UserMessagePopUp.do';
				break;
			case 'myMessage':
				result = te$.trendySite.act + '/applets/UserMessageInfoList.do';
				break;
			case 'myOrder':
				result = te$.trendySite.my + '/order/list.do';
				break;
			case 'cancelOrder':
				result = te$.trendySite.my + '/order/cancel.do';
				break;
			case 'myCoupon':
				result = te$.trendySite.my + '/coupon/list.do';
				break;
			case 'myInfo':
				result = te$.trendySite.passport + '/user/UserInfoManage.do';
				break;
			case 'cartURL':
				result = te$.trendySite.my + '/order/cart.do';
				break;
			case 'cartAdd':
				result = te$.trendySite.my + '/order/cartAdd.do';
				break;
			case 'cartModify':
				result = te$.trendySite.my + '/order/cartMod.do';
				break;
			case 'cartColorModify':
				result = te$.trendySite.my + '/order/cartModItem.do';
				break;
			case 'cartDelete':
				result = te$.trendySite.my + '/order/cartDel.do';
				break;
			case 'sso':
				result = te$.trendySite.sso + '/passport/info.do';
				break;
			case 'login':
				result = te$.trendySite.passport + '/user/Logon.do';
				break;
			case 'logout':
				result = te$.trendySite.passport + '/user/Logout.do';
				break;
			case 'account':
				result = te$.trendySite.my + '/user/Home.do';
				break;
			case 'wishlist':
				result = te$.trendySite.my + '/order/favorite.do';
				break;
			case 'onlineservice':
				result = 'http://183.63.71.221:8081/client.jsp?companyId=7&style=324&locate=cn&username=';
				break;
			case 'host':
				result = te$.trendySite.www;
				break;
			case 'proInfo':
				result = te$.trendySite.www + '/m/CommodityInventoryQuery.action';
				break;
			case 'favoriteAdd':
				result = te$.trendySite.my + '/order/favoriteAdd.do';
				break;
			case 'favoriteDel':
				result = te$.trendySite.my + '/order/favoriteDel.do';
				break;
			case 'orderConfirm':
				result = te$.trendySite.my + '/order/confirm.do';
				break;
			case 'address':
				result = te$.trendySite.passport + '/user/UserAddressManageJson.do';
				break;
			case 'addressCountry':
				result = ('https:' == document.location.protocol ? te$.trendySite.passport : te$.trendySite.www) + '/m/CountryList.action';
				break;
			case 'addressProvince':
				result = ('https:' == document.location.protocol ? te$.trendySite.passport : te$.trendySite.www) + '/m/ProvinceList.action';
				break;
			case 'addressCity':
				result = ('https:' == document.location.protocol ? te$.trendySite.passport : te$.trendySite.www) + '/m/CityList.action';
				break;
			case 'addressRegion':
				result = ('https:' == document.location.protocol ? te$.trendySite.passport : te$.trendySite.www) + '/m/RegionList.action';
				break;
			case 'couponVerify':
				result = te$.trendySite.my + '/coupon/verify.do';
				break;
			case 'removeMessage':
				result = te$.trendySite.act + '/applets/UserMessageInfoDel.do';
				break;
			case 'pointExchange':
				result = te$.trendySite.my + '/order/virtualProductBuy.do';
				break;
			case 'catalogItemList':
				result = te$.trendySite.www + '/m/ProductList.action';
				break;
			case 'couponAvailAble':
				result = te$.trendySite.my + '/coupon/CouponUserAvailAble.do';
				break;
			case 'vipStatus':
				result = te$.trendySite.passport + '/user/UserWeixinGradeJson.do';
				break;
			default:
				result = '';
		}
		return result;
	};
	te$.domainName = location.host.indexOf('fiveplus.com') >= 0 ? '.fiveplus.com' : location.host.indexOf('ochirly.com') >= 0 ? '.ochirly.com.cn' : '.trendiano.com';
	te$.sizeList = {'1':'XS', '2':'S', '3':'M', '4':'L', '5':'XL', '6':'XXL', '7':'XXXL', '9':'均', 'all':'', 'XS':'XS', 'S':'S', 'M':'M', 'L':'L', 'XL':'XL', '均码':'均', '30':'30', '31':'31', '32':'32', '33':'33', '34':'34', '35':'35', '36':'36', '37':'37', '38':'38', '39':'39', '40':'40', '41':'41', '42':'42', '43':'43', '44':'44', '45':'45'};
	te$.sizeListArray = ['1','2','3','4','5','6','7','9','all','XS','S','M','L','XL','均码','30','31','32','33','34','35','36','37','38','39','40','41','42','43','44','45'];/*用于尺码排序*/

	//单独的函数
	te$.place = location.hostname.split(".")[0] == 'test' ? location.hostname.split(".")[1] : location.hostname.split(".")[0];

	te$.myStyle = {
		'ochirly' : 'my_ochirly.css',
		'trendiano' : 'my_trendiano.css'
	};

	te$.myWapStyle = {
		'ochirly' : 'my_och.css',
		'trendiano' : 'my_trendiano.css',
		'fiveplus' : 'my_fp.css'
	};

	te$.setMyStyle = function() {
		var css, url;
		if (!(te$.getCurrBrand() in te$.myStyle)) return;
		css = document.createElement('link');
		url = (te$.checkTest() ? '//test.static.t-e-shop.com/trendy/global/v1/css/' : te$.trendySite.img2 + '/rs/common/v1/web/css/') + te$.myStyle[te$.getCurrBrand()];
		css.rel = "stylesheet";
		css.type = "text/css";
		css.href = url;
		document.getElementsByTagName("head")[0].appendChild(css);
	};

	te$.setMyWapStyle = function() {
		var	css = document.createElement('link'),
			url = (te$.checkTest() ? '//test.static.t-e-shop.com/trendy/global/v1/wap/css/' : te$.trendySite.img2 + '/rs/common/v1/wap/css/') + te$.myWapStyle[te$.getCurrBrand()];

		if (!(te$.getCurrBrand() in te$.myWapStyle)) return;

		css.rel = 'stylesheet';
		css.href = url;
		document.getElementsByTagName('head')[0].appendChild(css);
	};

	te$.myShowQrcode = function() {
		if (te$.getCurrBrand() != 'trendiano') {
			if (t$('navQrcode')) {
				t$('navQrcode').onmouseover = function() {
					this.parentNode.getElementsByTagName('div')[0].style.display = 'block';
				}
				t$('navQrcode').onmouseout = function() {
					this.parentNode.getElementsByTagName('div')[0].style.display = 'none';
				}
			}			
		}
	};

	te$.wash = {
		'ochirly' : {
			'自然风干':'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohBEIAAAAAAAKd5WvRmoAAM-7QHqLoQAAAqP965.png', 
			'专业护理':'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhU1NohBEIAAAAAAAHw2GGsTEAAM-7QH64o4AAAfb646.png', 
			'用湿布擦拭即可':'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhU1NohBEIAAAAAAAMY6PeD00AAM-7QIIyWUAAAx7483.png',
			'中温蒸汽熨烫': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohCgIAAAAAAAMOsdkMx0AAM-7gP_15EAAAxS446.png',
			'专业干洗': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohCgIAAAAAAAOFVZaI_MAAM-7gP_4-MAAA4t850.png',
			'低温蒸汽熨烫': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohCkIAAAAAAAMdNDyN5gAAM-7gP_8hAAAAyM936.png',
			'反面洗涤': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhU1NohCkIAAAAAAAOIudbRjIAAM-7wEd6FIAAA46602.png',
			'撑开平放干衣': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhVVNohCkIAAAAAAAIUYRsQssAANFkgP_o3EAAAhp773.png',
			'高温熨烫': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohCoIAAAAAAALqYSBQwoAAM-7wE-AsMAAAvB091.png',
			'悬挂晾干': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhVVNohCoIAAAAAAAJvLjIsRYAANFkgP_wVgAAAnU020.png',
			'不可曝晒': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhU1NohCoIAAAAAAALG_w4pq4AAM-7wFVav8AAAsz862.png',
			'不可漂白': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohCoIAAAAAAAJ1AaXvVUAAM-7wFbHgQAAAns332.png',
			'不可水洗': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhVlNohCsIAAAAAAAKmA_wOOIAANFkgP_1jUAAAqw081.png',
			'不可熨烫':'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhV1NohCwIAAAAAAALlDze-NgAANFkgP_4OUAAAus674.png',
			'不可干洗': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhUlNohCwIAAAAAAAMJBfNTHkAAM-7wHbwwIAAAw8512.png',
			'30℃以下网袋缓和机洗': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhV1NohC0IAAAAAAANvoHZQ8kAANFkgP_7JEAAA3W643.png',
			'30℃以下网袋机洗': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhV1NohC0IAAAAAAAMr3bs3FwAANFkwEQzGUAAAzH385.png',
			'30℃以下轻柔手洗': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhVlNohC0IAAAAAAAHD7PwEQMAANFkQP_-AcAAAf5478.png',
			'30℃以下常规洗涤': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhVlNohC4IAAAAAAAMpJsYXE0AANFkwE8m3sAAAy8327.png',
			'低温熨烫': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohC4IAAAAAAALdkj9MsUAAM-7wJsSeIAAAuO760.png',
			'请勿浸泡': 'http://img10.360buyimg.com/imgzone/g14/M0A/02/13/rBEhV1NohC4IAAAAAAALAyv0ZQ0AANFkwFHy_oAAAsb045.png',
			'请与同类颜色衣物洗涤': 'http://img10.360buyimg.com/imgzone/g13/M0A/01/0C/rBEhVFNohC4IAAAAAAAOPeMO5KQAAM-7wJ8_PoAAA5V856.png'
		},
		'fiveplus' : {
			'专业护理' : 'http://img01.taobaocdn.com/imgextra/i1/685140573/T2bu94XfRbXXXXXXXX_!!685140573.gif', 
			'自然风干' : 'http://img04.taobaocdn.com/imgextra/i4/685140573/T2HzcmXlhbXXXXXXXX_!!685140573.gif', 
			'专业干洗' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T2ow.zXjtXXXXXXXXX_!!685140573.gif' , 
			'中温蒸汽熨烫' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T27_UyXeFaXXXXXXXX_!!685140573.gif', 
			'悬挂晾干' : 'http://img01.taobaocdn.com/imgextra/i1/685140573/T22.v7Xh4aXXXXXXXX_!!685140573.gif', 
			'用湿布擦拭即可' : 'http://img03.taobaocdn.com/imgextra/i3/685140573/T2E6KEXhhcXXXXXXXX_!!685140573.gif', 
			'请勿浸泡' : 'http://img03.taobaocdn.com/imgextra/i3/685140573/T2y2cyXgNaXXXXXXXX_!!685140573.gif' , 
			'请与同类颜色衣物洗涤' : 'http://img04.taobaocdn.com/imgextra/i4/685140573/T2r87yXmNaXXXXXXXX_!!685140573.gif', 
			'高温熨烫' : 'http://img04.taobaocdn.com/imgextra/i4/685140573/T2ClwzXhxXXXXXXXXX_!!685140573.gif', 
			'低温蒸汽熨烫' : 'http://img01.taobaocdn.com/imgextra/i1/685140573/T26g.zXXRaXXXXXXXX_!!685140573.gif', 
			'反面洗涤' : 'http://img01.taobaocdn.com/imgextra/i1/685140573/T26AgzXglXXXXXXXXX_!!685140573.gif', 
			'不可熨烫' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T2rvIyXoFaXXXXXXXX_!!685140573.gif', 
			'低温熨烫' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T2KV.zXf4aXXXXXXXX_!!685140573.gif', 
			'不可水洗' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T2jbIzXfxaXXXXXXXX_!!685140573.gif', 
			'撑开平放干衣' : 'http://img04.taobaocdn.com/imgextra/i4/685140573/T2uS3yXcdaXXXXXXXX_!!685140573.gif', 
			'不可漂白' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T2N2AlXo4aXXXXXXXX_!!685140573.gif', 
			'不可暴晒' : 'http://img03.taobaocdn.com/imgextra/i3/685140573/T2gByUXeRbXXXXXXXX_!!685140573.gif', 
			'不可曝晒' : 'http://img03.taobaocdn.com/imgextra/i3/685140573/T2gByUXeRbXXXXXXXX_!!685140573.gif', 
			'不可干洗' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T26O7zXoXXXXXXXXXX_!!685140573.gif', 
			'30℃以下网袋机洗' : 'http://img03.taobaocdn.com/imgextra/i3/685140573/T2mkY5Xk8aXXXXXXXX_!!685140573.gif', 
			'30℃以下网袋缓和机洗' : 'http://img02.taobaocdn.com/imgextra/i2/685140573/T2XzZyXlXaXXXXXXXX_!!685140573.gif', 
			'30℃以下轻柔手洗' : 'http://img01.taobaocdn.com/imgextra/i1/685140573/T24YXSXlpeXXXXXXXX_!!685140573.gif', 
			'30℃以下常规洗涤' : 'http://img01.taobaocdn.com/imgextra/i1/685140573/T2eDCzXk8cXXXXXXXX_!!685140573.gif'
		},
		'trendiano' : {
			'自然风干':'http://img02.taobaocdn.com/imgextra/i2/1778137875/T2NAs5XeJaXXXXXXXX_!!1778137875.png',
			'专业干洗':'http://img02.taobaocdn.com/imgextra/i2/1778137875/T2FNU5XcdaXXXXXXXX_!!1778137875.png',
			'专业护理':'http://img03.taobaocdn.com/imgextra/i3/1778137875/T2eCQ6XgpXXXXXXXXX_!!1778137875.png',
			'中温蒸汽熨烫':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2pc37XXVXXXXXXXXX_!!1778137875.png',
			'用湿布擦拭即可':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2nNU6XlxXXXXXXXXX_!!1778137875.png',
			'低温蒸汽熨烫':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T295duXaROXXXXXXXX_!!1778137875.png',
			'反面洗涤':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2BiE6Xi0XXXXXXXXX_!!1778137875.png',
			'撑开平放干衣':'http://img02.taobaocdn.com/imgextra/i2/1778137875/T23BA4XltaXXXXXXXX_!!1778137875.png',
			'高温熨烫':'http://img01.taobaocdn.com/imgextra/i1/1778137875/T2lEc6XfJXXXXXXXXX_!!1778137875.png',
			'悬挂晾干':'http://img03.taobaocdn.com/imgextra/i3/1778137875/T2.tw7XXlXXXXXXXXX_!!1778137875.png',
			'不可曝晒':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2i4xVXjXeXXXXXXXX_!!1778137875.png',
			'不可漂白':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2KF6jXhBbXXXXXXXX_!!1778137875.png',
			'不可水洗':'http://img02.taobaocdn.com/imgextra/i2/1778137875/T2GJc7XXJXXXXXXXXX_!!1778137875.png',
			'不可熨烫':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2OHE6XbRaXXXXXXXX_!!1778137875.png',
			'不可干洗':'http://img01.taobaocdn.com/imgextra/i1/1778137875/T2UAZ5XiJaXXXXXXXX_!!1778137875.png',
			'30℃以下网袋缓和机洗':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2yVk5XihaXXXXXXXX_!!1778137875.png',
			'30℃以下网袋机洗':'http://img03.taobaocdn.com/imgextra/i3/1778137875/T2NCs5XaRaXXXXXXXX_!!1778137875.png',
			'30℃以下轻柔手洗':'http://img02.taobaocdn.com/imgextra/i2/1778137875/T2CEA5XcpaXXXXXXXX_!!1778137875.png',
			'30℃以下常规洗涤':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2AaMqXXVaXXXXXXXX_!!1778137875.png',
			'低温熨烫':'http://img04.taobaocdn.com/imgextra/i4/1778137875/T2Vy7MXk4aXXXXXXXX_!!1778137875.png',
			'请勿浸泡':'http://img01.taobaocdn.com/imgextra/i1/1778137875/T2DAI6XgxXXXXXXXXX_!!1778137875.png',
			'请与同类颜色衣物洗涤':'http://img02.taobaocdn.com/imgextra/i2/1778137875/T2Fkf7XoJaXXXXXXXX_!!1778137875.png'
		}
	};

	te$.traffic = function() {
		var	_gaqNow = new Date(),
			_gaqTime = '',
			userServiceId = (function(){
				var userId = '';
				userId = te$.cookie('sui') ? te$.cookie('sui') : '-1002'; 
				// if ('business' in te$) {
				// 	userId = te$.cookie('sui') ? te$.cookie('sui') : '-1002';
				// }
				// else {
				// 	userId = te$.business.login.getUserId() ? te$.business.login.getUserId() : '-1002';
				// }
				return userId;
			})();

		if (te$.checkTest()) return;

		window._gaq = window._gaq || [];
		//GA param
		if (te$.cookie('gaqUnique') == undefined) te$.cookie('gaqUnique', 'trendy_' + parseInt(Math.random() * (900000000000000) + 100000000000000), {
			expires: 730,
			path: '/',
			domain: te$.domainName
		});
		_gaqTime += _gaqNow.getFullYear();
		_gaqTime += (_gaqNow.getMonth() + 1).toString().length == 1 ? '0' + (_gaqNow.getMonth() + 1).toString() : (_gaqNow.getMonth() + 1).toString();
		_gaqTime += _gaqNow.getDate().toString().length == 1 ? '0' + _gaqNow.getDate().toString() : _gaqNow.getDate().toString();
		_gaqTime += _gaqNow.getHours().toString().length == 1 ? '0' + _gaqNow.getHours().toString() : _gaqNow.getHours().toString();
		_gaqTime += _gaqNow.getMinutes().toString().length == 1 ? '0' + _gaqNow.getMinutes().toString() : _gaqNow.getMinutes().toString();
		_gaqTime += _gaqNow.getSeconds().toString().length == 1 ? '0' + _gaqNow.getSeconds().toString() : _gaqNow.getSeconds().toString();
		_gaqTime += _gaqNow.getMilliseconds().toString().length == 1 ? '00' + _gaqNow.getMilliseconds() : _gaqNow.getMilliseconds().toString().length == 2 ? '0' + _gaqNow.getMilliseconds() : _gaqNow.getMilliseconds();

		_gaq.push(['_setAccount', 'UA-38772164-1']);
		_gaq.push(['_setDomainName', te$.domainName.substr(1)]);
		_gaq.push(['_setAllowLinker', true]);
		_gaq.push(['_setCustomVar', '1', 'uservisits', _gaqTime + '_' + te$.cookie('__utma') + '_' + userServiceId + '_' + te$.cookie('gaqUnique'), '3']);
		_gaq.push(['_trackPageview']);

		if (typeof(_gaViewPoint1) != 'undefined') {
			_gaq.push(['_addTrans', _gaOrderInfo.id + '_' + _gaqTime, _gaOrderInfo.shop, _gaOrderInfo.total, 0, _gaOrderInfo.ship]);
			for (var i = 0; i < _gaSkuItem.length; i++) {
				_gaq.push(['_addItem', _gaOrderInfo.id + '_' + _gaqTime, _gaSkuItem[i][0], _gaSkuItem[i][1], '', _gaSkuItem[i][2].substr(1), _gaSkuItem[i][3]]);
			}
			_gaq.push(['_trackTrans']);
		}
		//ga js
		var ga = document.createElement('script');
		ga.type = 'text/javascript';
		ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		var sGa = document.getElementsByTagName('script')[0];
		sGa.parentNode.insertBefore(ga, sGa);

		//start baidu
		window._hmt = window._hmt || [];
		try {
			if (te$.domainName != '.trendiano.com') {
				var atBaidu = document.createElement('script');
				atBaidu.type = 'text/javascript';
				atBaidu.async = true;
				if (te$.domainName == '.ochirly.com.cn') atBaidu.src = ('https:' == document.location.protocol ? 'https://hm.baidu.com/h.js?b0adbe94cc9ad3e2220d23cda6f79273' : 'http://hm.baidu.com/h.js?b0adbe94cc9ad3e2220d23cda6f79273');
				if (te$.domainName == '.fiveplus.com') atBaidu.src = ('https:' == document.location.protocol ? 'https://hm.baidu.com/h.js?ee21e2133af66bfcae5a0dfed24c75ed' : 'http://hm.baidu.com/h.js?ee21e2133af66bfcae5a0dfed24c75ed');
				var s1Baidu = document.getElementsByTagName('script')[0];
				s1Baidu.parentNode.insertBefore(atBaidu, s1Baidu);			
			}
		} catch (e) {}
		//end baidu

		if (te$.domainName != '.trendiano.com') {

			//聚合统计变量
			window._mvq = window._mvq || [];
			if (te$.domainName == '.fiveplus.com') {
				_mvq.push(['$setAccount', 'm-29247-0']);
			} else {
				_mvq.push(['$setAccount', 'm-29245-0']);
			}		
			//聚合统计
			//_mvq.push(['$setGeneral', '', '', /*用户名*/ '', /*用户id*/ '']);//如果不传用户名、用户id，此句可以删掉
			if (typeof(proInfo) != 'undefined') {
				//单品详情页
				_mvq.push(['$setGeneral', 'goodsdetail', '', '', '']);
				_mvq.push(['$logConversion']);
				//_mvq.push(['setPageUrl', /*单品着陆页url*/ '']); //如果不需要特意指定单品着陆页url请将此语句删掉
				_mvq.push(['$addGoods', /*分类id*/ '', te$.getCurrBrand(), proInfo.proName, proInfo.sku, proInfo.price, document.getElementsByTagName('img')[0].src, /*分类名*/ '', te$.getCurrBrand(), /*商品库存状态1或是0*/ '', proInfo.listPrice, /*收藏人数*/ '', /*商品下架时间*/ '']);

			}
			//home page
			if (window.location.href == 'http://www.ochirly.com.cn/' || window.location.href == 'http://www.fiveplus.com/') {
				_mvq.push(['$setGeneral', 'index', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//search
			if (window.location.href.indexOf('/search/') >= 0 && window.location.href.indexOf('target') == -1) {
				_mvq.push(['$setGeneral', 'searchresult', '', /*用户名*/ '', /*用户id*/ '']);
				_mvq.push(['$addSearchResult', /*搜素关键词*/ pageParams.name, /*搜索结果数*/ '']);
			}
			//category
			if (window.location.href.indexOf('list') >= 0 && window.location.href.indexOf('.shtml') >= 0 && window.location.href.indexOf('target') == -1) {
				_mvq.push(['$setGeneral', 'category', '', /*用户名*/ '', /*用户id*/ '']);
				_mvq.push(['$addCategory', /*分类名称*/ pageParams.name, /*分类ID*/ '']);
			}
			//user home
			if (window.location.href.indexOf('/user/Home.do') >= 0 && window.location.href.indexOf('target') == -1) {
				_mvq.push(['$setGeneral', 'memberindex', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//my order
			if (window.location.href.indexOf('/order/list.do') >= 0) {
				_mvq.push(['$setGeneral', 'memberorder', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//my score
			if (window.location.href.indexOf('/user/UserScoreList.do') >= 0) {
				_mvq.push(['$setGeneral', 'memberpoint', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//my favorite
			if (window.location.href.indexOf('/order/favorite.do') >= 0) {
				_mvq.push(['$setGeneral', 'memberfavorite', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//my comm
			if (window.location.href.indexOf('/applets/UserReviewInfoList.do') >= 0) {
				_mvq.push(['$setGeneral', 'comment', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//login
			if (window.location.href.indexOf('/user/UserAccountLogin.do') >= 0) {
				_mvq.push(['$setGeneral', 'login', '', /*用户名*/ '', /*用户id*/ '']);
			}
			//register
			if (window.location.href.indexOf('/user/UserAccountRegister.do') >= 0) {
				_mvq.push(['$setGeneral', 'register', '', /*用户名*/ '', /*用户id*/ '']);
			}

			_mvq.push(['$logData']);
			_mvq.push(['$logConversion']);

			var mvl = document.createElement('script');
			mvl.type = 'text/javascript';
			mvl.async = true;
			mvl.src = ('https:' == document.location.protocol ? 'https://static-ssl.mediav.com/mvl.js' : 'http://static.mediav.com/mvl.js');
			var s = document.getElementsByTagName('script')[0];
			s.parentNode.insertBefore(mvl, s);		
		}		
	};
	$(document).ready(te$.traffic);

	/*te$.generate = function(items) {
		var r = {};
		if (!items || items instanceof Array) return r
		else {
			for (var i = 0; i < items.length; i++) {
				r[items[i]] = items[i];
			}
			return r;
		}
	}*/

	te$.message = (function(window, te$, $, undefined){
		var 
		remove = function(id) {
			$.ajax({
				url : te$.getUrl('removeMessage'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'removeMessage:' + id, 'messageId' : id},
				jsonpCallback : 'trendyCall'
			});
		},

		removeItem = function(o) {
			var messageInfo;

			if ('code' in o) {
				if (o.code == 1) {
					messageInfo = o.command.split(':');
					if (messageInfo.length == 2) {
						$(t$('messageItem' + messageInfo[1])).fadeOut(200);
						return;
					}
				}
				te$.ui.msgBox.show('删除未能成功，再试一次看看。');
			}
		},

		setRemove = function() {
			$('b.btn_delete').each(function(){
				if (this.getAttribute('data-id')) {
					$(this).bind('click', function() {
						remove(this.getAttribute('data-id'));
					});
				}
			});
		}

		return {
			'setRemove' : setRemove,
			'removeItem' : removeItem
		}
	})(window, te$, $);	
