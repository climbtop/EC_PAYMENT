te$.business = (function(window, te$, $, undefined){
	var 
	/*当前用户信息*/
	user = {
		hasLogin : false,
		loginId : null,
		userName : null,
		userLevel : null,		
		userType : 'G',
		userId : null,
		shortName : null
	},

	/*
	 * 登录控制:
	 * connect : 获取接口数据，判断登录状态并设置用户信息
	 * checkLogin : 返回当前用户的登录状态
	 * showRelateInfo : 相关弹出信息的现实
	 */
	login = {
		loginTime : 0,
		clearLogin : function() {
			//te$.cookie('client_info', cValue, {'domain':te$.domainName, 'path':'/'});
			te$.cookie('client_info',null,{'path':'/', 'domain':te$.domainName});
			te$.cookie('CART_NUM',null,{'path':'/', 'domain':te$.domainName});
		},
		getUserLoginInfo : function() {
			var script = document.createElement('script');
			script.setAttribute('src', te$.getUrl('sso') + '?t=' + Math.random());
			script.setAttribute("defer", "defer");
			document.getElementsByTagName("head")[0].appendChild(script);
		},
		connect : function() {
			var	cValue = null,
				userInfoCookie = te$.cookie('client_info'),
				trendyUid = te$.cookie('sui'), 
				infoArray;
			//console.log(userInfoCookie + '...' + trendyUid);	
			/*if (!userInfoCookie && !trendyUid || !userInfoCookie && trendyUid || userInfoCookie && !trendyUid) {*/
			if (!userInfoCookie || !trendyUid) {
				if (userInfoCookie && userInfoCookie == 'false^^^^G^^'){

				}
				else {
					this.getUserLoginInfo();
					return;				
				}				
			}

			if (trendyUid && trendyUid != '0' && userInfoCookie == 'false^^^^G^^') {
					this.getUserLoginInfo();
					return;					
			}

			infoArray = userInfoCookie.split('^');
			if (infoArray.length == 7) {
				if (infoArray[5] != trendyUid && infoArray[0] == 'true') {
					this.getUserLoginInfo();
					return;
				}
				else {
					user.hasLogin =  infoArray[0] == 'false' ? false : true;
					user.userName = infoArray[2];
					user.userType = infoArray[4];
					user.logonId = infoArray[1];
					user.userLevel = infoArray[3];
					user.userId = infoArray[5];
					user.shortName = infoArray[6];						
				}
			}
			else {
				this.getUserLoginInfo();
				return;
			}

			cart.updateCartNum();
			login.showRelateInfo();	
		},
		checkLogin : function() {
			return user.hasLogin;
		},
		getUser : function() {
			return user.userName;
		},
		getUserId : function() {
			return user.userId;
		},
		goLogin : function() {
			window.location.href = te$.getUrl('login');
		},
		getUserType : function() {
			return user.userLevel;
		},
		showRelateInfo : function() {
			var	userServiceId =login.getUserId()?login.getUserId() : '-1002',
				onlineServiceUrl = te$.getUrl('onlineservice') + userServiceId,
				_inServiceOpen = function() {
					window.open(onlineServiceUrl, 'onlineServiceOpenWin', 'height=640, width=840, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no', true);
					return false;
				};

			$('#linkServiceOnline, .linkServiceOnlineTop, #linkServiceOnlineFoot, .linkServiceOnlineFoot1, .linkServiceOnlineProduct').bind('click', _inServiceOpen);
			$('#linkCart').attr('href', te$.getUrl('cartURL')); //购物袋
			//$('#shoppingIcon').attr('href', te$.getUrl('cartURL'));浮动购物袋
			//$('#popCartNum').attr('href', te$.getUrl('cartURL'));浮动购物袋数量
			$('#linkAccount').attr('href',te$.getUrl('account')); //我的账户
			$('#linkWishlist').attr('href', te$.getUrl('wishlist'));  //收藏
			$('.link_home_icon').attr('href', te$.getUrl('host')) ;

			if (login.checkLogin()) {
				$('#paramLogin').html('<em id="emUserName">' +login.getUser() + '，您好！</em><a href="#" id="linkLogin">退出</a>');
				$('#linkLogin').html('退出').attr('href',te$.getUrl('logout'));
				$('#linkLogin').click(function(){
					var cValue = 'false^^^^G^^';
					te$.cookie('client_info', cValue, {'domain':te$.domainName, 'path':'/'});
					te$.cookie('CART_NUM', 0, {'path':'/', 'domain':te$.domainName});
				});
			} else {
				$('#paramLogin').html('<a href="' +  te$.getUrl('login') + '" id="linkLogin">登录 • 注册</a>');
			}
			cart.updateCartNum();			
		},
		getUserScore : function() {
			$.ajax({
				url : te$.getUrl('userScore'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'userScore'},
				jsonpCallback : 'trendyCall',
				complete : function() {
					//1
					te$.business.login.getUserMessage();
				}
			});
		},
		getUserMessage : function() {
			$.ajax({
				url : te$.getUrl('userMessage'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'userMessage'},
				jsonpCallback : 'trendyCall',
				complete : function() {
					//2
					te$.business.login.getUserCouponNum();
				}
			});
		},
		getUserCouponNum : function() {
			$.ajax({
				url : te$.getUrl('couponAvailAble'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'userCouponNum'},
				jsonpCallback : 'trendyCall',
				complete : function() {
					//3
					te$.business.login.getUserVip();
				}
			});
		},
		getUserVip : function(stop) {
			$.ajax({
				url : te$.getUrl('vipStatus'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'userVipStatus'},
				jsonpCallback : 'trendyCall',
				complete : function() {
					//3
					if (stop != true) te$.business.login.getMyNewOrder();
				}
			});
		},
		getMyNewOrder : function() {
			$.ajax({
				url : te$.getUrl('myOrder'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'myTopOrder', 'type' : 'json', 'pageSize' : '5'},
				jsonpCallback : 'trendyCall',
				complete : function() {
					//4
					te$.business.login.getNewInItems();
				}
			});
		},
		getNewInItems : function() {
			var	brand = te$.getCurrBrand(),
				p = {
					storeId : te$.storeId[brand],
					catalogId : te$.newInCatalogId[brand],
					pageSize : 4
				}

			$.ajax({
				url : te$.getUrl('catalogItemList'),
				dataType : 'jsonp',
				cache : false,
				data : {'action' : 'getNewInItems', 'storeId' : p.storeId, 'pageSize' : p.pageSize, 'catalogId' : p.catalogId},
				jsonpCallback : 'trendyCall'
			});
		},
		showUserCouponNum : function(o) {
			if ('code' in o && o.code == 1) {
				if (t$('txUserCouponNum')) t$('txUserCouponNum').innerHTML = o.content;
			}
		},
		showVipStatus : function(o) {
			var	v = 'message' in o ? o.message.toLowerCase() : 'general',
				m = null;

			//if (window.location.href.indexOf('/user/Home.do') >= 0) {
				if (v.indexOf('vip') == 0) {
					m = v.split(':');
					if (t$('txUserVipLevel')) {
						t$('txUserVipLevel').innerHTML = 'vip会员：' + m[1];
					}
					$('.my_name').eq(0).find('p').eq(1).html('vip会员');
					if (t$('linkBindVip')) {
						t$('linkBindVip').style.display = 'none';
					}					
				}
			//}
		},
		showUserScore : function(o) {
			var c;
			if (window.location.href.indexOf('/user/Home.do') >= 0) {
				if ('code' in o) {
					c = o.content;
					if (c.userProfileBeanList.length > 0) {
						if (t$('txUserScore')) t$('txUserScore').innerHTML = parseInt(c.userProfileBeanList[0].userScore);
						// if (t$('txUserVipLevel')) {
						// 	if (c.userProfileBeanList[0].membershipCard) {
						// 		t$('txUserVipLevel').innerHTML = 'vip会员：' + c.userProfileBeanList[0].membershipCard
						// 	}
						// }						
					}
					else {
						if (t$('txUserScore')) t$('txUserScore').innerHTML = 0;
						// if (t$('txUserVipLevel')) {
						// 	t$('txUserVipLevel').innerHTML = '非VIP会员';
						// }
					}

					if (t$('txProgress')) {
						t$('txProgress').getElementsByTagName('span')[0].style.width = parseInt(parseFloat(c.userInfoCompleted)) + '%';
						t$('txProgress').getElementsByTagName('b')[0].innerHTML = parseInt(parseFloat(c.userInfoCompleted)) + '%'
					}
				}
			}
		},
		showNewInItems : function(o) {
			var	b = '<ul class="item_list">{c}</ul>',
				c = '<li class="col_4_1 cell"><div class="chip"><a target="_blank" href="{url}"><img alt="{title}" src="{img}"><span class="chip_brand">{brand}</span><span class="chip_name">{title}</span></a><p class="chip_price"><span>￥{price}</span></p></div></li>',
				item = '',
				oi;
			if ('code' in o && o.code == 1) {
				for (var i = 0, l = o.content.productList.length; i < l; i++) {
					oi = o.content.productList[i];
					item += te$.subStitute(c, {
						'url' : te$.trendySite.www + '/p/' + oi.sku + '.shtml',
						'img' : te$.skuToImgUrl(oi.sku, 'list', 1),
						'title' : oi.productShortTitle,
						'brand' : te$.getCurrBrand(),
						'price' : oi.listPrice
					});
				}

				if (item) {
					item = te$.subStitute(b, {'c' : item});
				}
			}

			if (!item) item = '<div class="blank_tip blank_tip_short"><b class="blank_tip_no">no</b>还没有最新的推荐：（</div>';
			$('#newInList')
			.html(item)
			.removeClass('loading');
		},
		showUserMessage : function(o) {
			var	c,
				date,
				myMessageUrl = te$.getUrl('myMessage'),
				temp = '<h3 class="col_6_1 cell">消息<span class="tip_num">{num}</span></h3><div class="col_6_5 cell"><div class="col_9_8 cell"><p class="col_5_3 cell"><a href="{url}">{title}</a><span>[{date}]</span></p><p class="col_5_2 cell"><a href="{url}">查看详细</a></p></div><div class="my_message_close col_9_1 cell"><b>x</b></div></div>';
			if (window.location.href.indexOf('/user/Home.do') >= 0) {
				if ('code' in o) {
					c = o.content;
					if (c.userMessageInfoSize > 0) {
						if (t$('myMessageTip')) {
							date = new Date(c.createTime);
							t$('myMessageTip').innerHTML = te$.subStitute(temp, {
								'url' : myMessageUrl,
								'num' : c.userMessageInfoSize,
								'title' : c.messageTitle,
								'date' : date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDay()
							});
							t$('myMessageTip').style.display = 'block';
							t$('myMessageTip').getElementsByTagName('b')[0].onclick = function() {
								t$('myMessageTip').style.display = 'none';
							}
						}						
					}

				}
			}
		},
		showMyNewOrder : function(o) {
			var	c,
				tempTable = '<table class="cart_table"><thead><tr><th class="col_3_1">订单号</th><th class="col_12_1">商品数量</th><th class="col_6_1">金额</th><th class="col_6_1">订单状态</th><th class="col_6_1">操作</th></tr></thead><tbody>{content}</tbody></table>',
				tempTd = '<tr><td><div class="my_item_product"><a target="_blank" href="{url}"><img src="{img}"></a><p><a target="_blank" href="{url}">{orderId}</a><br /><span>{date}</span></p></div></td><td>{num}</td><td><b>{price}</b></td><td><div class="my_item_status"><p>{status}</p></div></td><td><div class="my_item_action">{action}</div></td></tr>',
				html = '',
				action = '';
			if ('code' in o) {
				if (o.code == 1) {
					c = o.content;
					if (c.length > 0) {
						for (var i = 0, l = c.length; i < l; i++) {
							action = '<p><a href="' + te$.trendySite.my + '/order/detail.do?orderId=' + c[i].orderId + '" target="_blank">查看订单</a></p>';
							if (c[i].orderStatus == 'WAIT_PAY') {
								action += '<p><a href="' + te$.trendySite.my + '/order/pay.do?orderId=' + c[i].orderId + '&_=' + (new Date()).valueOf() + '" target="_blank">继续支付</a></p>'
							}
							html += te$.subStitute(tempTd, {
								'url' : te$.trendySite.my + '/order/detail.do?orderId=' + c[i].orderId,
								'img' : te$.skuToImgUrl(c[i].orderItemList[0].productItemNumber.substr(0, 13), 's', 1),
								'orderId' : c[i].orderId,
								'date' : (new Date(c[i].createTime)).toLocaleString(),
								'num' : c[i].orderItemList.length,
								'price' : '￥' + c[i].orderFactAmount.toFixed(2),
								'status' : te$.orderStatus[c[i].orderStatus],
								'action' : action
							});
						}
						html = te$.subStitute(tempTable, {
							'content' : html
						});
						$('#myHomeOrderList').removeClass('loading');
						$('#myOrderItem').html(html).show();
						return;
					}
				}
			}
			$('#myHomeOrderList').removeClass('loading');
			$('#myOrderListBlank').show();
		},
		setLoginView : function() {
			var imgs = {
				'ochirly' : te$.checkTest() ? 'url(//test.static.t-e-shop.com/trendy/global/v1/css/login_ochirly.jpg)' : 'url(//img2s' + te$.domainName + '/rs/common/images/login/login_ochirly.jpg)',
				'fiveplus' : te$.checkTest() ? 'url(//test.static.t-e-shop.com/trendy/global/v1/css/login.jpg)' : 'url(//img2s' + te$.domainName + '/rs/common/images/login/login.jpg)',
				'trendiano' : te$.checkTest() ? 'url(//test.static.t-e-shop.com/trendy/global/v1/css/login_trendiano.jpg)' : 'url(//img2s' + te$.domainName + '/rs/common/images/login/login_trendiano.jpg)'
			}

			if (t$('linkHome')) t$('linkHome').href = te$.trendySite.www;
			if (t$('loginFrame')) t$('loginFrame').style.backgroundImage = imgs[te$.getCurrBrand()];

			if (te$.getCurrBrand() == 'trendiano') $('.login_oauth').hide();
		}
	},
	
	/*
	 *购物袋
	 * goods : 购物袋内商品数据
	 * getData : 获取购车车数据
	 * fill : 填写购物袋
	 * add : 添加商品到购物袋
	 * remove : 移除购物袋内的某件商品 
	 * favorites : 添加商品到收藏夹
	*/
	cart = {
		cartShowTimer : null,
		cids : {'num' : 1},
		currCid : {'cid': null, 'size': null, 'num': null, 'pid': null},
		cartIds : '',

		useCouponCode : function(code) {
			coupon.useCoupon(code);
		},
		uploadCart : function(itemInfo) {
			if (this.cartShowTimer) window.clearTimeout(this.cartShowTimer);
			this.cartShowTimer = window.setTimeout(function(){
				cart.modify(itemInfo);
			}, 500);
		},
		countCart : function(list) {
			var	table = t$(list);
			if (!table) return false;

			var	allCheck = te$.getByClass('ck_select_item', 'input', table),
				totalPrice = 0,
				totalItem = 0;

			this.cartIds = '';
			for (var i = 0; i < allCheck.length; i++) {
				if (allCheck[i].checked) {
					totalPrice += this.cartData[allCheck[i].value].price * this.cartData[allCheck[i].value].quantity;
					totalItem += this.cartData[allCheck[i].value].quantity;
					//this.cartIds += this.cartData[allCheck[i].value].cartId + ',';
					this.cartIds += this.cartData[allCheck[i].value].itemId + ',' + this.cartData[allCheck[i].value].quantity + ';'
				}
			}
			if (this.cartIds.length > 1) {
				this.cartIds = this.cartIds.substr(0, this.cartIds.length - 1);
			}

			t$('cartItemCount').innerHTML = totalItem;
			t$('cartTotalPrice').innerHTML = te$.formatMoney(totalPrice);

			if (totalItem == 0) t$('btnCheckout').disabled = true
			else t$('btnCheckout').disabled = false;
		},
		clearCart : function() {
			this.cartIds = '';
			cart.updateCartNum(); 
			cart.cartData = {};
			$('#myBag').parent().append('<div class="blank_tip"><b class="blank_tip_no">no</b>购物袋为空啦：（</div>');
			$('#myBag').remove();
			$('.order_total').remove();
		},
		checkoutCart : function() {
			//this.countCart('myBag');
			if (this.cartIds == '' || this.cartIds.length < 1) {
				te$.ui.msgBox.show('还未选中任何一件商品。');	
				return false;
			} else {
				window.location.href = te$.getUrl('orderConfirm') + '?orderItemStr=' + this.cartIds + '&_=' + (new Date()).valueOf();
			}
		},
		checkForPay : function() {

		},
		initCart : function() {
			$('.link_del_cart_item').each(function(){
				$(this).bind('click', function(){
					cart.remove($(this).attr('data-action-param'));
				});
			});

			$('.size_package a').each(function(){
				$(this).bind('click', function(){
					rechoose.rechooseItem(this);
				});
			});

			$('.link_add_fav').each(function(){
				$(this).bind('click', function(){
					favorite.add($(this).attr('data-action-param'));
				});
			});

			$('.btn_num_cut').each(function(){
				$(this).bind('click', function(){
					var 	itemId = $(this).attr('data-item-id'),
						input = t$('qty' + itemId),
						productId = this.getAttribute('data-product-id'),
						cartId = this.getAttribute('data-cart-id');

					if (parseInt(input.value) <= 1) {
						return false;
					}
					else {
						input.value = parseInt(input.value) - 1;
						cart.uploadCart({'cartId':cartId, 'productId':productId, 'num':input.value, 'itemId':itemId});
					}
				});
			});

			$('.btn_num_plus').each(function(){
				$(this).bind('click', function(){
					var 	itemId = $(this).attr('data-item-id'),
						input = t$('qty' + itemId),
						productId = this.getAttribute('data-product-id'),
						cartId = this.getAttribute('data-cart-id');

					input.value = parseInt(input.value) + 1;
					cart.uploadCart({'cartId':cartId, 'productId':productId, 'num':input.value, 'itemId':itemId});
				});
			});

			$('.ck_select_item').each(function(){
				$(this).bind('click', function(){
					var	allCheck,
						allChecked = true;
					cart.countCart('myBag');
					if (!this.checked) {
						if (t$('ckSelectAll').checked) t$('ckSelectAll').checked = false
					}
					else {
						if (!t$('ckSelectAll').checked) {
							var allCheck = te$.getByClass('ck_select_item', 'input', t$('myBag'));
							for (var i = 0; i < allCheck.length; i++) {
								if (!allCheck[i].checked) allChecked = false;
							}
							if (allChecked) t$('ckSelectAll').checked = true;
						}
					}
				});
			});
			
			t$('ckSelectAll').onchange = function() {
				var allCheck = te$.getByClass('ck_select_item', 'input', t$('myBag'));
				for (var i = 0; i < allCheck.length; i++) {
					allCheck[i].checked = this.checked;
				}
				cart.countCart('myBag');
			};

			$('#btnDeleteSelected').bind('click', function(){
				var table = t$('myBag');
				if (!table) return false;

				var	allCheck = te$.getByClass('ck_select_item', 'input', table),
					itemIds = '';

				for (var i = 0; i < allCheck.length; i++) {
					if (allCheck[i].checked) itemIds += allCheck[i].value + ',';
				}
				if (itemIds.length > 0) {
					itemIds = itemIds.substr(0, itemIds.length - 1);
					cart.remove(itemIds);
				} else {
					te$.ui.msgBox.show('没有选中的商品。');
				}
			});

			$('#btnFavoriteSelected').bind('click', function(){
				var table = t$('myBag');
				if (!table) return false;

				var	allCheck = te$.getByClass('ck_select_item', 'input', table),
					itemIds = '';

				for (var i = 0; i < allCheck.length; i++) {
					if (allCheck[i].checked) itemIds += allCheck[i].value + ',';
				}
				if (itemIds.length > 0) {
					itemIds = itemIds.substr(0, itemIds.length - 1);
					favorite.add(itemIds);
				} else {
					te$.ui.msgBox.show('没有选中的商品。');
				}
			}).hide();

			t$('btnCheckout').onclick = function(){
				te$.cookie('couponTip', null);
				cart.checkoutCart();
			};

			this.countCart('myBag');
		},
		initCheckout : function() {
			var	couponCode,
				couponChange = false,
				checkConfirm = function(){
					var f = t$('orderInfo');
					if (!f.country.value || !f.province.value || !f.city.value || !f.region.value || !f.address.value || !f.mobile.value || !f.receiverName.value) 
						return false
					else 
						return true;
				};

			//order checkout
			$('#btnCheckout').bind('click', function(){
				if (t$('userMessage').value.length > 0) {
					t$('userComment').value = t$('userMessage').value;
				}

				if (checkConfirm()) 
					t$('orderInfo').submit()
				else {
					te$.ui.msgBox.show('请填写好收货信息。');
				}
			});

			$('#btnShowAllAddress').bind('click', function(){
				$('#addressSelect .address_item').each(function(){
					if (!$(this).hasClass('selected')) {
						if (this.getAttribute('data-opend') == 'true') {
							$(this).fadeOut();
							this.removeAttribute('data-opend');
						} else {
							$(this).fadeIn();
							this.setAttribute('data-opend', 'true');
						}
					}
				});
			});

			//address change
			$('#addressSelect input[type="radio"]')
			.each(function(){
				var	oldId = t$('addressId').value;

				if (this.value == oldId) {
					this.checked = true;
					t$('countryModify').value = this.getAttribute('data-country');
					t$('provinceModify').value = this.getAttribute('data-province');
					t$('cityModify').value = this.getAttribute('data-city');
				}
			})
			.bind('click', function(){
				var addressId = $(this).attr('value');
				// for (var i in address.books[addressId]) {
				// 	if (t$(i)) t$(i).value = address.books[addressId][i];
				// }
				if (t$('addressId')) t$('addressId').value = addressId;
				if (t$('countryModify')) t$('countryModify').value = address.books[addressId].country;
				if (t$('cityModify')) t$('cityModify').value = address.books[addressId].city;
				if (t$('provinceModify')) t$('provinceModify').value = address.books[addressId].province;

				if (t$('checkoutModify')) t$('checkoutModify').submit();
				//address.fillCheckoutAddress(addressId);	
			});

			$('.address_item').bind('click', function(){
				var	currAddressId = this.getElementsByTagName('input')[0].value,
					orderAddressId = t$('addressId').value;

				if (currAddressId != orderAddressId) this.getElementsByTagName('input')[0].click();
			});
			//record old address

			//check used coupon, remove the same one
			if (t$('couponCodeStrModify')) {
				if (t$('couponCodeStrModify').value.length > 0) {
					couponCode = t$('couponCodeStrModify').value.split(';');
					coupon.queryCoupon = couponCode;
					if (couponCode.length > 1) {
						for (var i = couponCode.length - 1; i >= 1; i--) {
							for (var j = i - 1; j >= 0; j--) {
								if (couponCode[i] == couponCode[j]) {
									couponCode.splice(i, 1);
									couponChange = true;
									break;
								}
							}
						}
						if (couponChange) {
							t$('orderInfo').value = t$('couponCodeStrModify').value = couponCode.join(';');
							coupon.queryCoupon = couponCode;
						}
					}
				}
			}

			//use coupon
			$('.link_private_coupon').each(function(){
				var	code = $(this).attr('data-coupon-code'),
					used = false;

				for (var i = 0; i < coupon.queryCoupon.length; i++) {
					if (coupon.queryCoupon[i] == code) {
						used = true;
						break;
					}
				}

				if (used) {
					$(this).bind('click', function() {
						coupon.useCoupon(code, true);
					});
					$(this).html('取消');
				} 
				else {
					$(this).bind('click', function() {
						coupon.useCoupon(code);
					});
				}
			});

			//input coupon code
			$('#btnUseCoupon').bind('click', function() {
				var	code = t$('userCouponCode').value,
					used = false;

				if (!code) {
					te$.ui.msgBox.show('需要输入优惠码', [
						['确定', function(){
							t$('userCouponCode').focus();
							te$.ui.msgBox.hide();
						}]
					]);
					return;
				}

				used = coupon.checkCoupon(code);

				if (used) {
					te$.ui.msgBox.show('优惠券已使用。');
				} else {
					coupon.verifyCode(code);
					//coupon.useCoupon(code);
				}
			});

			$('#couponList').find('b').each(function(){
				var	currCode = this.getAttribute('data-coupon-code'),
					currId = this.getAttribute('data-promotion-id'),
					used = coupon.checkCoupon(currCode),
					validity = coupon.checkValidity(currCode, currId);

				if (used) {
					if (validity) {
						this.innerHTML = this.innerHTML.substr(2, 1);
						this.title = '取消使用';
						this.className = 'btn_used';
						this.setAttribute('data-action', 'delete');	
					}
					else {
						if (te$.cookie('couponTip') != 'done') {
							te$.ui.msgBox.show('您选择的优惠券暂不能使用。');
						}
						t$('couponCodeStr').value = coupon.removeCoupon(t$('couponCodeStr').value, currCode);
						this.innerHTML = '∅';
						this.title = '此券无效';
						//this.className = 'btn_disable';
						this.setAttribute('class', 'btn_disable');
						this.setAttribute('data-action', 'disable');
					}
				} else {
					this.innerHTML = this.innerHTML.substr(0, 1);
					this.title = '使用';
					this.setAttribute('data-action', 'add');
				}

				if (this.getAttribute('class') != 'btn_disable') {
					this.addEventListener('click', function(){
						var	code = this.getAttribute('data-coupon-code'),
							action = this.getAttribute('data-action') == 'delete' ? true : false;

						coupon.useCoupon(code, action);
					});
				}
			});

			address.fillCheckoutAddress();
		},
		getData : function(asyType, fillData) {
			//var	site = te$.getUrl('cartURL'); // + '?type=json';
			/*	asyType = asyType || false; //同步获取或者异步获取
			if (fillData) var fillData = fillData //是否更新购物袋
			else var fillData = true;*/
			if (window.location.protocol != 'https:') {
				$.ajax({
					url : te$.getUrl('cartURL') + '?type=json',
					dataType : 'jsonp',
					cache : false,
					data : {'action' : 'getCartInfo'},
					jsonpCallback : 'trendyCall'
				});				
			}
		},

		/*update cart number after delete item from cart*/
		//, #popCartNum, #popBoxCartNum
		updateCartNum : function(list) {
			$('#cartNum').html(te$.cookie('CART_NUM') ? parseInt(te$.cookie('CART_NUM')) : 0);
		},
		fill : function(reflashNum) {
			if (reflashNum) {
				this.getData(true);
			} else {
				this.updateCartNum(); 
			}
		},
		postAdd : function(num) {
			var postCatalogId = window.location.href.indexOf('://test.') > 0 ? '1508702' : '4197001';

			var quantityNum = 1;
			if (num) quantityNum = num;

			try {
				$.ajax({
					url : te$.getUrl('cartAdd'), 
					dataType : 'json',
					cache: false,
					data : 'catEntryId=' + postCatalogId + '&quantity=' + quantityNum,
					success : function(data) {
						if(data.message['status'] == 'OK') {
							te$.cookie('CART_NUM',parseInt(te$.cookie('CART_NUM')) + 1,{'path':'/', 'domain':te$.domainName});
							cart.gotoCart();
						} else {
							te$.ui.msgBox.show('遇到错误：' + data.message['message']);
						}
					},
					error : function(data) {
						te$.ui.msgBox.show('加入购物袋失败，你的账号登录可能已经超时或者在其他地方登录了，请重新登录',[
							['重新登录', function(){window.location.href = te$.getUrl('login');}],
							['关闭', function(){window.location.reload();}]
						]);
					}
				});	
			} catch(e) {
				te$.ui.msgBox.show('添加购物袋过程中发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);
			}
		},
		afterAdd : function(o) {
			if ('code' in o && o.code == 1) {
				te$.ui.msgBox.show(cart.currCid.num + '件' + te$.gloGetSize(cart.currCid.size) + '码成功放入购物袋', [
					['继续购物', function(){te$.ui.msgBox.hide();}, 'btn_red'],
					['结算', function(){
						// if(!user.hasLogin) location.href = te$.getUrl('login'); 
						// else 
						cart.gotoCart()
					}]
				]);
				if (!user.hasLogin)	{
					var linkLogin = document.createElement('a');
					linkLogin.href = te$.getUrl('login');
					linkLogin.innerHTML = '登录 • 注册';
					linkLogin.style.marginLeft = '1em';

					var txTip = document.createElement('p');
					txTip.innerHTML = '亲爱的顾客：<br />建议您先登录再将商品放入购物袋，确保您能随心收纳两个品牌的商品，并一次性完成付款。';
					txTip.style.textAlign = 'left';
					txTip.style.color = '#aaaaaa';
					txTip.style.marginBottom = '25px';


					if (t$('msgBtns')) {
						t$('msgBtns').appendChild(linkLogin);
						t$('msgBtns').parentNode.insertBefore(txTip, t$('msgBtns'));
					}
				}

				//te$.cookie('CART_NUM',null,{'path':'/', 'domain':te$.domainName});
				cart.fill(true);			}
			else {
				te$.ui.msgBox.show('添加购物袋遇到错误，请再试试');
			}
		},
		add : function(diret) {
			var action = 'addCart';
			if (!cart.currCid.size || !cart.currCid.cid || !cart.currCid.num) {
				if (t$('noSizeChoose')) {
					t$('noSizeChoose').style.display = 'block';	
				}
				return false;
			}

			if (diret == 'reflash') action = 'reflashCart';

			try {
				$.ajax({
					url : te$.getUrl('cartAdd'),
					dataType : 'jsonp',
					cache : false,
					data : {'itemId':cart.currCid.cid, 'quantity':1, 'productId':cart.currCid.pid, 'action':action},
					jsonpCallback : 'trendyCall'
				});
			} catch(e) {
				te$.ui.msgBox.show('添加购物袋过程中发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);	
			}
		},
		cancelOrder : function(id, fromDetail) {
			if (!id) return;
			te$.ui.msgBox.show('确定要取消此订单吗？', [
				['确定', function(){
					try {
						$.ajax({
							url : te$.getUrl('cancelOrder'),
							dataType : 'jsonp',
							data : {'orderId':id, 'action':'cancelOrder:' + id + (fromDetail ? ':d' : '')},
							jsonpCallback : 'trendyCall'
						});
					} catch(e) {
						te$.ui.msgBox.show('删除异常，请稍候再试');	
					}
				}],
				['取消', function(){
					te$.ui.msgBox.hide();
				}]
			]);
		},
		afterCancelOrder : function(o) {
			var actionInfo;
			if ('code' in o && o.code == 1) {
				actionInfo = o.command.split(':');
				if (actionInfo.length == 3) {
					te$.ui.msgBox.hide();
					if (document.referrer) window.location.href = document.referrer
					else window.history.go(-1);
				}
				else {
					if (t$('orderItem' + actionInfo[1])) {
						$(t$('orderItem' + actionInfo[1])).fadeOut();
						te$.ui.msgBox.hide();
					}
				}
			}
			else te$.ui.msgBox.show('删除异常，请稍候再试');
		},
		setCancelOrder : function() {
			$('.linkCancelOrder').bind('click', function(){
				var id = this.getAttribute('data-id');
				if (!id) return;

				cart.cancelOrder(id);
			});
		},
		deleteOrder : function(id, fromDetail) {
			if (!id) return;
			te$.ui.msgBox.show('确定要删除此订单吗？', [
				['确定', function(){
					try {
						$.ajax({
							url : te$.getUrl('cancelOrder'),
							dataType : 'jsonp',
							data : {'orderId':id, 'action':'cancelOrder:' + id + (fromDetail ? ':d' : '')},
							jsonpCallback : 'trendyCall'
						});
					} catch(e) {
						te$.ui.msgBox.show('删除异常，请稍候再试');	
					}
				}],
				['取消', function(){
					te$.ui.msgBox.hide();
				}]
			]);
		},
		afterDeleteOrder : function(o) {
			var actionInfo;
			if ('code' in o && o.code == 1) {
				actionInfo = o.command.split(':');
				if (actionInfo.length == 3) {
					te$.ui.msgBox.hide();
					if (document.referrer) window.location.href = document.referrer
					else window.history.go(-1);
				}
				else {
					if (t$('orderItem' + actionInfo[1])) {
						$(t$('orderItem' + actionInfo[1])).fadeOut();
						te$.ui.msgBox.hide();
					}
				}
			}
			else te$.ui.msgBox.show('删除异常，请稍候再试');
		},
		setDeleteOrder : function() {
			$('.linkDeleteOrder').bind('click', function(){
				var id = this.getAttribute('data-id');
				if (!id) return;

				cart.deleteOrder(id);
			});
		},
		oneKeyBuy : function() {
			if (!cart.currCid.size || !cart.currCid.cid || !cart.currCid.num) {
				if (t$('noSizeChoose')) {
					t$('noSizeChoose').style.display = 'block';	
				}
				return false;
			}

			window.location.href = te$.getUrl('orderConfirm') + '?orderItemStr=' + cart.currCid.cid + ',1' + '&_=' + (new Date()).valueOf();
		},
		modify : function(itemInfo) {
			try {
				$.ajax({
					url : te$.getUrl('cartModify'),
					dataType : 'jsonp',
					cache : false,
					data : {'quantity' : itemInfo.num, 'cartId' : itemInfo.cartId, 'action' : 'modifyCart:' + itemInfo.itemId},
					jsonpCallback : 'trendyCall'
				});				
			}
			catch(e) {
				te$.ui.msgBox.show('删除购物袋过程中发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);
			}
		},
		afterModify : function(o) {
			if (o.hasOwnProperty('code')) {
				if (o.code > 0) {
					var	actionInfo = o.command.split(':'),
						cartData = cart.cartData[actionInfo[1]];
					cartData.quantity = parseInt(t$('qty' + actionInfo[1]).value);
					t$('spPrice'  + actionInfo[1]).innerHTML = te$.formatMoney(cartData.price * cartData.quantity, '￥');
					//t$('spPoint'  + actionInfo[1]).innerHTML = cartData.price * cartData.quantity;
					cart.countCart('myBag');
					cart.updateCartNum();
				}
				else {
					te$.ui.msgBox.show('操作未能完成，请再试一次。');
				}
			}
			else {
				te$.ui.msgBox.show('发生错误，请再试一次。');
			}
		},		
		remove : function(orderItemId) {
			if (!orderItemId) return false;
			try {		
				$.ajax({
					url : te$.getUrl('cartDelete'),
					dataType : 'jsonp',
					cache : false,
					data : {'itemIds':orderItemId, 'action':'deleteCartItem:' + orderItemId},
					jsonpCallback : 'trendyCall'
				});
			} 
			catch(e) {
				te$.ui.msgBox.show('删除购物袋过程中发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);
			}			
		},
		afterRemove : function(o) {
			if (o.hasOwnProperty('code')) {
				if (o.code > 0) {
					var	actionInfo = o.command.split(':'),
						items = actionInfo[1].split(','),
						tr = '';

					for (var i = 0; i < items.length; i++) {
						if (i == 0) tr += '#item' + items[i]
						else tr += ', #item' + items[i];

						delete cart.cartData[items[i]];
					}

					$(tr).animate({
							'opacity':0
						}, 
						500,
						function() {
							var l = te$.cookie('CART_NUM') ? parseInt(te$.cookie('CART_NUM')) : 0;
							$(tr).remove();
							cart.updateCartNum();
							if (l == 0) cart.clearCart('myBay')
							else {
								cart.countCart('myBag');								
							}
						}
					);
				}
				else {
					te$.ui.msgBox.show('操作未能完成，请再试一次。');
				}
			}
			else {
				te$.ui.msgBox.show('发生错误，请再试一次。');
			}
		},
		favorites : function(id){
			if (login.checkLogin()) {
				/*if (!cart.currCid.cid) {
					t$('noSizeChoose').style.display = 'block';
					return false;
				}*/
				var cid = id ? id : cart.currCid.cid;
				this.add(cid);
			} else {
				login.goLogin();
				return;
			}		
		},
		sizeWriter : function(s) {
			var	size = '',
				oneSize = '',
				showNoSize = false,
				sizeNum = 0,
				sizeId ='',
				sizeEntryId ='';
			if (s) {
				for (var i = 0; i < te$.sizeListArray.length; i++) {
					if (te$.sizeListArray[i] in s) {
						sizeNum += 1;
						sizeId = te$.sizeListArray[i];
						sizeEntryId = s[te$.sizeListArray[i]]['catEntryId'];
						if (s[te$.sizeListArray[i]]['quantity'] < 1) {
							oneSize += '<a href="javascript:;" class="btn_size_dis" title="暂时无货">' + te$.gloGetSize(te$.sizeListArray[i]) + '</a>';
							size += '<a href="javascript:;" class="btn_size_dis" title="暂时无货">' + te$.gloGetSize(te$.sizeListArray[i]) + '</a>'}
						else {
							
							oneSize += ' <a href="javascript:;" >' + te$.gloGetSize(te$.sizeListArray[i]) + '</a>';
							size += ' <a href="javascript:;"  onclick="te$.business.cart.setSize(\'' + sizeId + '\', \'' + sizeEntryId + '\' , this);">' + te$.gloGetSize(te$.sizeListArray[i]) + '</a>'; 
							showNoSize = true;
						}
					}
				}
				var source="linkRule";
				var target="sizeInfo";
				var sourceTop = $("#proColor").position().top + 100;
				oneSize += "<span href=javascript:; class=link_rule id=linkRule onclick=javascript:$('#sizeInfo').css({'display':'block','top':'" + sourceTop + "px'})>尺码表</span> <span class='no_size_choose' id='noSizeChoose' style='width:" + sizeNum * 28 + "px'>请选择尺码</span>"
				size += "<span href=javascript:; class=link_rule id=linkRule onclick=javascript:$('#sizeInfo').css({'display':'block','top':'" + sourceTop + "px'})>尺码表</span> <span class='no_size_choose' id='noSizeChoose' style='width:" + sizeNum * 28 + "px'>请选择尺码</span>"
			} else {
				//size = '<div class="pro_size_no" style="display:none">暂时无货</div>';
				//size = '<div class="pro_sell_out" id="proSellOut"><p>该商品已售罄</p><a  class="btn_sell_out" href="${notifierUrl}?partNumber=${product.partNumber}" target="_blank">到货通知</a></div>'
				t$('proSellOut').style.display = "block";
			}
			if ((sizeNum == 1) && (showNoSize == true)) {
				t$('groupChooseSize').innerHTML = oneSize;
				te$.business.cart.setSize(sizeId, sizeEntryId, t$('groupChooseSize').getElementsByTagName('a')[0]);	
			}
			else  t$('groupChooseSize').innerHTML = size;

			var input = function(event) {
				//补齐判断是否整数
				if (this.value == 0 || this.value == '' || isNaN(parseInt(this.value))) this.value = 1;
				cart.setNum(this.value);
			}
			var press = function(event) {
				if ( !((event.which >= 48 && event.which <= 57) || (event.which >= 96 && event.which <= 105) || event.which == 8 || event.which == 46 || event.which == 37 || event.which == 39) ) event.preventDefault();
				cart.setNum(this.value);
			}
			$('#pickNum').focus(input).blur(input).keydown(press);	

			if (proInfo.inventoryTag == '即将发售') {
				t$('proSellOutTxt').innerHTML = '即将发售';
			} else {
				t$('proSellOutTxt').innerHTML = '该商品已售罄';
			}			

			if (!showNoSize) {
				t$('proSizeChoose').style.display = 'none';
				t$('proSellOut').style.display = 'block';
				t$('proBtnBox').style.display = 'none';
				t$('proColor').style.display = 'none';
				
			} else {
				if (proInfo.inventoryTag == '即将发售') {
					t$('proSizeChoose').style.display = 'none';
					t$('proSellOut').style.display = 'block';	
					t$('proBtnBox').style.display = 'none';	
					t$('proColor').style.display = 'none';
				}
				else{
					t$('proSizeChoose').style.display = 'block';
					t$('proSellOut').style.display = 'none';	
					t$('proBtnBox').style.display = 'block';	
					t$('proColor').style.display = 'block';
				}
			}
		},
		showSize : function(cid) {
			var s, size = '';
			if (cid != stock.cid) s = stock.get(cid, true, true)
			else {
				s = stock.items;
				cart.sizeWriter(s);
			}
			return;
			
			//此处设法与group中获取尺码共用
			if (s) {
				size += '<span>选择尺码：</span>';
				for (var i = 0; i < te$.sizeListArray.length; i++) {
					if (te$.sizeListArray[i] in s) {
						if (s[te$.sizeListArray[i]]['quantity'] < 1) size += '<a href="javascript:;" class="btn_size_dis" title="暂时无货">' + te$.gloGetSize(te$.sizeListArray[i]) + '</a>'
						else size += '<a href="javascript:;"  onclick="te$.business.cart.setSize(\'' + te$.sizeListArray[i] + '\', \'' + s[te$.sizeListArray[i]]['catEntryId'] + '\' , this);">' + te$.gloGetSize(te$.sizeListArray[i]) + '</a>';
					}
				}
				size += '<span class="label_num">数量：</span><input type="text" name="pickNum" id="pickNum" value="1" />';
			} else {
				size = '<span>尺码：<em>缺货</em></span>';
			}
			t$('groupChooseSize').innerHTML = size;
			
			var input = function(event) {
				//补齐判断是否整数
				if (this.value == 0 || this.value == '' || isNaN(parseInt(this.value))) this.value = 1;
				cart.setNum(this.value);
			}
			var press = function(event) {
				if ( !((event.which >= 48 && event.which <= 57) || (event.which >= 96 && event.which <= 105) || event.which == 8 || event.which == 46 || event.which == 37 || event.which == 39) ) event.preventDefault();
				cart.setNum(this.value);
			}
			$('#pickNum').focus(input).blur(input).keydown(press);
		},
		setNum : function(num) {
			if (!num) return;
			cart.currCid.num = num;
		},
		unsetSize : function() {
			alert('该尺码暂时无货。');
			ochirly.te$.cookie('proSize', null);
			ochirly.te$.cookie('cid', null);
		},
		setSize : function(size, id, e, g, mId) {
			if (!id || !size) return;
			t$('noSizeChoose').style.display = 'none';
			var cid = 'cid' + id;
			var l = e.parentNode.getElementsByTagName('a'), elementId = '';
			
			var clear = function() {
				for (var i = 0; i < l.length; i++) {
					if (l[i] == e) e.className = 'active'
					else if (l[i].className != 'btn_size_dis') {
						l[i].className = ''};
				}				
			}
			
			if (g == 2 && mId) {
				/*1230elementId = 'pickNum_' + mId;
				if (e.className == 'active') {
					e.className = '';
					group.groupBuyCids[mId] = {'cid': null, 'size': null, 'num': null, 'choose': true}
					return;
				} else {
					group.groupBuyCids[mId] = {'cid': id, 'size': size, 'num': t$(elementId).value || 1, 'choose': !t$('noPickNum_' + mId).checked}
				}*/
			} else {
				/*1230if (g == 1) {						
					elementId = 'preOrderNum';
				} else {
					elementId = 'pickNum';
				}*/
				if (e.className == 'active') {
					//delete this.cids[cid];
					e.className = '';
					this.currCid.cid = null;
					this.currCid.size = null;
					this.currCid.num = null;
					this.currCid.choose = true;
					this.currCid.pid = null;
					return;
				}

				this.currCid.cid = id;
				this.currCid.pid = proInfo.cId;
				this.currCid.size = size;
				//选择数量 this.currCid.num = t$(elementId).value || 1;
				this.currCid.num = 1;
				this.currCid.choose = true;
			}
			clear();
		},
		gotoCart : function() {
			window.location.href = te$.getUrl('cartURL');
		},
		rechooseShow : function(o) {
			if (!o) return;
			rechoose.fillData(o);
		}
	},

	address = {
		books : {},
		fillCheckoutAddress : function(addressId) {
			if (t$('checkoutAddress')) {
				if (!addressId) {
					for (var i in this.books) {
						if (this.books[i].isDefault != 0) {
							t$('checkoutAddress').innerHTML = this.books[i].province + this.books[i].city + this.books[i].region + this.books[i].address;
							t$('checkoutReceiver').innerHTML = this.books[i].receiverName + ' ' + this.books[i].mobile;
						}
					}					
				} else {
					t$('checkoutAddress').innerHTML = this.books[addressId].province + this.books[addressId].city + this.books[addressId].region + this.books[addressId].address;
					t$('checkoutReceiver').innerHTML = this.books[addressId].receiverName + ' ' + this.books[addressId].mobile;					
				}
			}
		},
		fillOrderForm : function(addressId){
			for (var i in this.books[addressId]) {
				if (t$(i)) t$(i).value = this.books[addressId][i];
			}			
		},
		resetSelected : function(addressId) {
			$('.address_item').each(function(){
				$(this).removeClass('selected');
				if (this.getElementsByTagName('input')[0].value == addressId) $(this).addClass('selected');
			})
		}
	},

	rechoose = {
		newItemId : null,
		firstShow : true,
		rechooseItem : function(oldItem) {
			var content = t$('rechoose');
			if (!content) return;

			var	frame = content.parentNode,
				oldItemId = oldItem.getAttribute('data-item-id'),
				oldProductId = oldItem.getAttribute('data-product-id'),
				oldCartId = oldItem.getAttribute('data-cart-id');

			if (frame.getAttribute('data-curr-id') == oldCartId) {
				if ($(frame).css('display') == 'block') $(frame).fadeOut()
				else $(frame).fadeIn();
			}
			else {
				try {
					$.ajax({
						url : te$.getUrl('proInfo'),
						dataType : 'jsonp',
						cache : false,
						data : {'commodityId':oldProductId, 'action':'showSpu:' + oldItemId + ':' + oldProductId + ':' + oldCartId, 'storeId':te$.business.cart.cartData.storeId},
						jsonpCallback : 'trendyCall'
					});
				} 
				catch(e) {
					te$.ui.msgBox.show('数据请求错误：' + e.name + ':' + e.message);
				}			
				//frame.setAttribute('data-curr-id', oldItemId);	
			}
		},
		fillData : function(o) {
			var	ids,
				skus,
				html,
				content,
				frame,
				item,
				targetLink;
			if (o.code !== 0) {
				content = t$('rechoose');
				if (!content) return;
				frame = content.parentNode;
				ids = o.command.split(':');
				skus = o.content;
				/*
				<div class="rechoose_item">
					<P><img src="" /></P>
					<div>
						<a>XS</a>
						<a>S</a>
						<a>M</a>
						<a>L</a>
						<a>XL</a>
					</div>
				</div>
				*/
				html = '';
				for (var i = 0; i < skus.length; i++) {
					if (skus[i].productItemInventoryEntityList) {
						html += '<div class="rechoose_item">';
						html += '<p><a href="' + te$.trendySite.www + '/p/' + skus[i].sku + '.shtml" target="_blank" title="' + skus[i].colorName + '"><img src="' + te$.skuToImgUrl(skus[i].sku, 's', 1) + '" /></a></p>';
						html += '<div>';
						for (var k = 0; k < skus[i].productItemInventoryEntityList.length - 1; k++) {
							item = skus[i].productItemInventoryEntityList[k];

							if (item.quantity == 0)
								html += '<del>' + te$.getSizeDesc(item.sizeId) + '</del>'
							else
								if (item.productItemId == ids[1])
									html += '<span>' + te$.getSizeDesc(item.sizeId) + '</span>'
								else
									html += '<a href="javascript:;" data-product-id="' + skus[i].productId + '" data-item-id="' + item.productItemId + '">' + te$.getSizeDesc(item.sizeId) + '</a>';
						}
						html += '</div></div>';						
					}
				}
				
				content.innerHTML = html;
				targetLink = t$('linkRechoose' + ids[1]);
				if ($(frame).css('display') == 'none') {
					$(frame).css({
						'left' : $(targetLink).offset().left - $(frame).width() / 2,
						'top' : $(targetLink).offset().top
					}).fadeIn();
				}
				else {
					$(frame).css({
						'left' : $(targetLink).offset().left - $(frame).width() / 2,
						'top' : $(targetLink).offset().top
					});
				}
				frame.setAttribute('data-curr-id', ids[3]);

				this.newItemId = null;

				$(content).find('a').each(function(){
					$(this).bind('click', function(e){
						var itemId = this.getAttribute('data-item-id');
						if (!itemId) return
						else {
							rechoose.newItemId = itemId;

							cart.currCid.cid = this.getAttribute('data-item-id');
							cart.currCid.pid = this.getAttribute('data-product-id');
							cart.currCid.num = 1;
							cart.currCid.size = this.innerHTML;

							$(content).find('a').removeClass('selected');
							this.className = 'selected';
						}
					});
				});

				if (rechoose.firstShow) {
					$('#btnRechooseYes').unbind().bind('click', function(){
						if (rechoose.newItemId && rechoose.newItemId != ids[1]) {
							//alert(this.parentNode.parentNode.getAttribute('data-curr-id'));
							//cart.remove(this.parentNode.parentNode.getAttribute('data-curr-id'));
							//alert(cart.currCid.cid);
							//cart.add('reflash');

							///order/cartModItem.do?cardId=itemId=xxx&productId=yyy&quantity=zzz
							var param = {
								'cartId' : this.parentNode.parentNode.getAttribute('data-curr-id'),
								'itemId' : cart.currCid.cid,
								'productId' : cart.currCid.pid,
								'quantity' : 1,
								'action' : 'reflashCart'
							}
							try {		
								$.ajax({
									url : te$.getUrl('cartColorModify'),
									dataType : 'jsonp',
									cache : false,
									data : param,
									jsonpCallback : 'trendyCall'
								});
							} 
							catch(e) {
								te$.ui.msgBox.show('操作出现错误：' + e.name + ':' + e.message);
							}							
						} else {
							te$.ui.msgBox.show('未选择新的颜色和尺码。');
						}
					});
					$('#btnRechooseNo').bind('click', function(){
						$(content).find('a').removeClass('selected');
						rechoose.newItemId = null;
						$(frame).fadeOut();
					});
					rechoose.firstShow = false;
				}
			}
		}
	},

	favorite = {
		initFavorite : function() {
			$('.link_del_favorite_item').each(function(){
				$(this).bind('click', function(){
					favorite.remove($(this).attr('data-action-param'));
				});
			});
		},
		add : function(productId) {
			try {
				$.ajax({
					url : te$.getUrl('favoriteAdd'),
					dataType : 'jsonp',
					cache : false,
					data : {'productId' : productId, 'action' : 'addFavorite:' + productId},
					jsonpCallback : 'trendyCall'
				});				
			}
			catch(e) {
				te$.ui.msgBox.show('添加收藏夹过程中发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);
			}
		},
		afterAdd : function(o) {
			if (o.hasOwnProperty('code')) {
				if (o.code > 0) {
					te$.ui.msgBox.show('所选商品已放入收藏夹。');
				}
				else {
					te$.ui.msgBox.show('已经放入收藏夹的商品不需要重复添加。');
				}
			}
			else {
				te$.ui.msgBox.show('发生错误，请再试一次。');
			}
		},
		remove : function(productId) {
			try {
				$.ajax({
					url : te$.getUrl('favoriteDel'),
					dataType : 'jsonp',
					cache : false,
					data : {'productId' : productId, 'action' : 'removeFavorite:' + productId},
					jsonpCallback : 'trendyCall'
				});				
			}
			catch(e) {
				te$.ui.msgBox.show('发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);
			}			
		},
		afterRemove : function(o) {
			if (o.hasOwnProperty('code')) {
				if (o.code > 0) {
					var	actionInfo = o.command.split(':'),
						items = actionInfo[1].split(','),
						tr = '';

					for (var i = 0; i < items.length; i++) {
						if (i == 0) tr += '#fav' + items[i]
						else tr += ', #fav' + items[i];
					}

					$(tr).animate(
						{'opacity':0}, 
						500,
						function() {
							$(tr).remove();
							//todo : clearFavoriteList
							if ($('#myFavorite li').length == 0) {
								$('#myFavorite').parent().append('<div class="blank_tip"><b class="blank_tip_no">no</b>收藏夹空啦：（</div>');
								$('#myFavorite').remove();								
							};
						}
					);
				}
				else {
					te$.ui.msgBox.show('操作未能完成，请再试一次。');
				}
			}
			else {
				te$.ui.msgBox.show('发生错误，请再试一次。');
			}			
		}		
	},

	/*
	 *库存 
	*/
	stock = {
		cid : null, //商品ID
		availability : false,
		listPrice : 0, //吊牌价格
		offerPrice : 0, //成交价格
		items : null,
		get : function(cid, show, asyType) {
			cid = cid || this.cid;
			if (!cid) return false;

			var item,
			outOfStock = false;

			$.ajax({
				url : te$.getUrl('proInfo'),
				dataType : 'jsonp',
				cache : false,
				data : {'productId' : cid, 'action' : 'getProInfo:' + cid},
				jsonpCallback : 'trendyCall'
			});

			/*$.ajax({
				url : te$.getUrl('proInfo'),
				async : asyType,
				dataType : 'json',
				cache : false,
				data : 'catEntryId=' + cid, 
				success : function(data) {
					try {
						if (data.message.status == 'OK') {
							//var outOfStockNum = 0;
							var outOfStock = 0;
							item  = {}
							for (var i = 0; i < data.productInfo.itemInventorys.length; i++) {
								var k = data.productInfo.itemInventorys[i].size;
								item[k] = {}
								item[k].catEntryId = data.productInfo.itemInventorys[i].catEntryId;
								item[k].quantity = data.productInfo.itemInventorys[i].quantity;
								outOfStock += parseInt(data.productInfo.itemInventorys[i].quantity);
								//if (data.productInfo.itemInventorys[i].quantity == 0) outOfStockNum++;
							}
							//if (outOfStockNum == data.productInfo.itemInventorys.length) item['outOfStock'] = true;
							item.loyaltyPoint = data.productInfo.loyaltyPoint;
							item.listprice = data.productInfo.listprice;
							item.offerPrice = data.productInfo.offerPrice;
							item.isPointProduct = data.productInfo.isPointProduct;
							item.exchangePoint = data.productInfo.exchangePoint;
							item.outOfStock = outOfStock > 0 ? false : true;
							//item.listPrice = data.productInfo.listPrice;
							//item.offerPrice = data.productInfo.offerPrice;
						} 
					} catch (e) {
						item  = {}
					} 
							
					if (show && show == true) {

						//通过参数设置判定是否单品页显示尺码及相应的操作
						cart.sizeWriter(item);
						stock.setBuyButton(item);				
					}

					if (asyType == true) {
						window.clearInterval(te$.timeOutTimer);
						//document.title = 'clear';
						te$.timeOutCount = 0;
					}
				}
			});

			this.items = item;
			return item;*/
		},
		reset : function(o) {
			var	outOfStock = 0,
				s,
				p,
				item = {};

			if ('code' in o || o.code == 1) {
				if ('content' in o || o.content instanceof Array) {
					for (var i = 0; i < o.content.length; i++) {
						s = o.content[i];
						if (s.productId == this.cid) {
							p = s.productItemInventory;
							for (var k = 0; k < p.length; k++) {
								item[p[k].sizeId] = {}
								item[p[k].sizeId].catEntryId = p[k].productItemId;
								item[p[k].sizeId].quantity = p[k].itemInventory;
								outOfStock += parseInt(p[k].itemInventory);
							}
							//item.loyaltyPoint = proInfo.loyaltyPoint;
							item.listprice = proInfo.listprice;
							item.offerPrice = proInfo.offerPrice;
							//item.isPointProduct = proInfo.isPointProduct;
							//item.exchangePoint = proInfo.exchangePoint;
							item.outOfStock = outOfStock > 0 ? false : true;
							cart.sizeWriter(item);
							stock.setBuyButton(item);
							this.items = item;					
						}
					}	
				}
			}
		},
		setBuyButton : function(item) {
			var btnOrderBuy = $('#btnOrderBuy');
			var btnOrderWish = $('#btnOrderWish');
			var btnOneKeyBuy = $('#btnOrderOneKey');
			if (item.outOfStock) {
				btnOrderBuy.css("display","none")
				//t$('btnOrderBuy').style.display="none";
			} else {
				btnOrderBuy.click(function() {
					if (te$.business.stock.sku) {
						if (typeof _gaq != 'undefined') _gaq.push(['_trackEvent', '用户购买行为', '加入购物车', te$.business.stock.sku.toString()])
					}
					if (proInfo.buyAble == 1) te$.business.cart.add()
					else te$.ui.msgBox.show('该商品为礼品，不可单独购买');
				});
				//按钮变色，暂时去掉
				// btnOrderBuy.mouseover(function() {
				// 	btnOrderBuy.css("background-position","center -222px")
				// });
				// btnOrderBuy.mouseout(function() {
				// 	btnOrderBuy.css("background-position","center top")
				// });

				btnOneKeyBuy.click(function(){
					if (te$.business.stock.sku) {
						if (typeof _gaq != 'undefined') _gaq.push(['_trackEvent', '用户购买行为', '加入购物车', te$.business.stock.sku.toString()])
					}
					if (proInfo.buyAble == 1) te$.business.cart.oneKeyBuy();
					else te$.ui.msgBox.show('该商品为礼品，不可单独购买');
				});
			}
			btnOrderWish.click(function(){
				if (te$.business.stock.sku) {
					if (typeof _gaq != 'undefined') _gaq.push(['_trackEvent', '用户购买行为', '收藏商品', te$.business.stock.sku.toString()]);
				}
				if (proInfo.buyAble != 1) {
					te$.ui.msgBox.show('该商品为礼品，无需加入收藏');
					return false;
				}
				if(user.hasLogin){
					if (item.outOfStock) {
						te$.ui.msgBox.show('该商品已售罄，暂不能加入收藏');
					} 
					else {
						//te$.business.cart.favorites(proInfo.cId);
						//te$.business.
						favorite.add(proInfo.cId);
					}
				}
				else {
					//未登录状态
					location.href=te$.getUrl('login');
				}
			});	
			// btnOrderWish.mouseover(function(){
			// 	btnOrderWish.css("background-position","center -276px");
			// 	btnOrderWish.css("background-color","#eeeeee");
			// })
			// btnOrderWish.mouseout(function(){
			// 	btnOrderWish.css("background-position","center -54px");
			// 	btnOrderWish.css("background-color","#ffffff");
			// })
			$('#loyaltyPointValue').html(item.loyaltyPoint);
			$('#basePrice').html(item.offerPrice == item.listprice ? '<span>￥' + item.offerPrice + '</span>' : '<del>￥' + item.listprice + '</del><span>￥' + item.offerPrice + '</span>');
			

			if (item.isPointProduct == 'True') {
				//$('#exchangePoint').html('换购积分');
				//$('#loyaltyPointValue').html(stock.exchangePoint);
				$('#proPriceNum').html('￥' + item.listprice + '<span>兑换积分 : <font id="loyaltyPointValue">' + item.exchangePoint + '</font></span>');
			}

			if (proInfo.buyAble != 1) {
				$('#proBtnBox').hide();
			}	
		},
		showProSize : function() {
			var size = '';
			for (var i = 0; i < te$.sizeListArray.length; i++) {
				if (te$.sizeListArray[i] in proInfo.sizeItems) {
					size += '<a title="暂不可用" class="btn_size_dis" href="javascript:;">' + te$.sizeList[te$.sizeListArray[i]] +'</a>'
				}
			}

			t$('groupChooseSize').innerHTML = size;			

			/*if (proInfo.sizeItems && proInfo.sizeItems.length > 0) {
				for (var i = 0; i < proInfo.sizeItems.length; i++) {
					document.write('<a title="暂不可用" href="javascript:;">' + te$.sizeList[sizeItems[i][0]] +'</a>');
					if (codeSizeId == '') codeSizeId = proInfo.sizeItems[i][0];
				}
			}*/
		}
	},

	coupon = {
		queryCoupon : [],
		useCoupon : function(couponCode, remove) {
			var ccm;
			if (!couponCode) return false;

			if (t$('couponCodeStrModify')) {
				ccm = t$('couponCodeStrModify');

				if (remove) {
					ccm.value = ccm.value.replace(couponCode, '');
					ccm.value = ccm.value.replace(';;', ';');
					if (ccm.value.indexOf(';') == 0) ccm.value = ccm.value.substr(1);
					if (ccm.value.substr(ccm.value.length - 1) == ';') ccm.value = ccm.value.substr(0, ccm.value.length - 1);
				} else {
					if (ccm.value.length > 0) ccm.value += ';' + couponCode
					else ccm.value += couponCode;					
				}

				t$('userCommentModify').value = t$('userMessage').value;
				t$('checkoutModify').submit();
			}
		},
		verifyCode : function(code) {
			try {
				$.ajax({
					url : te$.getUrl('couponVerify'),
					dataType : 'jsonp',
					cache : false,
					data : {'couponCode' : code, 'action' : 'couponVerify'},
					jsonpCallback : 'trendyCall'
				});				
			}
			catch(e) {
				te$.ui.msgBox.show('发生错误，请和我们的客服联系并反映以下错误信息：' + e.name + ':' + e.message);
			}	
		},
		checkCoupon : function(currCode) {
			if (!t$('couponCodeStr') || !t$('couponCodeStr').value) return false;

			var	usedCode = t$('couponCodeStr').value.split(';'),
				used = false;
			for (var i = 0, l = usedCode.length; i < l; i++) {
				if (usedCode[i] == currCode) {
					used = true;
					break;
				}
			}
			return used;			
		},
		checkPromotion : function(currId) {
			if (!t$('userCouponCode') || !t$('userCouponCode').getAttribute('data-used-promotion-id')) return false;
			var	usedId = (t$('userCouponCode').getAttribute('data-used-promotion-id') ? t$('userCouponCode').getAttribute('data-used-promotion-id') : '').split(';'),
				used = false;

			for (var i = 0, l = usedId.length; i < l; i++) {
				if (usedId[i] == currId) {
					used = true;
					break;
				}
			}
			return used;
		},
		checkValidity : function(currCode, currId) {
			return this.checkCoupon(currCode) && this.checkPromotion(currId);
		},
		removeCoupon : function(s, code) {
			var	used = s.split(';'),
				tp = [];

			for (var i = 0, l = used.length; i < l; i++) {
				if (used[i] != code) tp.push(used[i]);
			}

			return tp.join(';');
		}
	},

	pay = {
		initPayChannel : function() {
			var	payForm = t$('payForm'),
				channelChoose = t$('payChannelChoose'),
				check = null;

			if (!payForm) return;

			check = function() {
				if (payForm.payId.value && payForm.channelId.value) return true
				else {
					te$.ui.msgBox.show('请选择支付方式');
					return false;	
				}
			}

			$('#payChannelChoose label').click(function(){
				var input = this.getElementsByTagName('input')[0];

				if (input) input.click();
			});

			$('#payChannelChoose input').click(function(){
				payForm.channelId.value = this.value;
				$(channelChoose).find('p').each(function(){
					this.removeAttribute('class');
				});
				this.parentNode.parentNode.setAttribute('class', 'selected');
				t$('imgPayChannel').src = this.parentNode.getElementsByTagName('img')[0].src;
				t$('imgPayChannel').parentNode.parentNode.style.display = 'inline';
			});

			$('#btnGotoPay').click(function(){
				if (check()) payForm.submit();
			});
		},

		waitWecharQrcodeScan : function(param) {
			var url = '/pay/CheckItemPayStatus.do';
			if (!param) return;

			window.setInterval(function(){
				$.ajax({
					url : url,
					dataType : 'json',
					cache : false,
					type : 'GET',
					data : param,
					success : function(data) {
						if ('code' in data && data.code == 1) {
							if (data.content.payStatus == 'PAYED') {
								window.location.href = data.content.callBackUrl;
							}
						}
					}
				});
			}, 10000);
		}
	};

	return {
		user : user,
		login : login,
		cart : cart,
		stock : stock,
		favorite : favorite,
		address : address,
		pay : pay
	}
})(window, te$, $);