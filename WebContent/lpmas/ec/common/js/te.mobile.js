te$.mobile = (function(window, te$, $, undefined){
	var
	user = {
		hasLogin : false,
		loginId : null,
		userName : null,
		userLevel : null,		
		userType : 'G',
		userId : null,
		shortName : null
	},

	clickAndTouch = te$.detectMobile() ? 'click' : 'click',

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

	cart = {
		cartShowTimer : null,
		cartIds : '',
		cartData : {},

		useCouponCode : function(code) {
			coupon.useCoupon(code);
		},

		uploadCart : function(itemInfo) {
			if (this.cartShowTimer) window.clearTimeout(this.cartShowTimer);
			this.cartShowTimer = window.setTimeout(function(){
				cart.modify(itemInfo);
			}, 500);
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
					//cart.updateCartNum();
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
							//cart.updateCartNum();
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

		/*cart action*/
		initCart : function() {
			$('#btnCartCheckout').bind(clickAndTouch, function(){
				te$.cookie('couponTip', null);
				cart.checkoutCart();
			});

			$('.btn_num_cut').each(function(){
				$(this).bind(clickAndTouch, function(){
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

			$('.link_del_cart_item').each(function(){
				$(this).bind(clickAndTouch, function(){
					cart.remove($(this).attr('data-action-param'));
				});
			});

			$('.btn_num_plus').each(function(){
				$(this).bind(clickAndTouch, function(){
					var 	itemId = $(this).attr('data-item-id'),
						input = t$('qty' + itemId),
						productId = this.getAttribute('data-product-id'),
						cartId = this.getAttribute('data-cart-id');

					input.value = parseInt(input.value) + 1;
					cart.uploadCart({'cartId':cartId, 'productId':productId, 'num':input.value, 'itemId':itemId});
				});
			});			

			this.countCart('myBag');
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

			if (totalItem == 0) t$('btnCartCheckout').disabled = true
			else t$('btnCartCheckout').disabled = false;
		},

		clearCart : function() {
			this.cartIds = '';
			//cart.updateCartNum(); 
			cart.cartData = {};
			$('#myBag').parent().append('<div class="blank_tip"><b class="blank_tip_no">no</b>购物袋为空啦：（</div>');
			$('#myBag').remove();
		},

		checkoutCart : function() {
			if (this.cartIds == '' || this.cartIds.length < 1) {
				te$.ui.msgBox.show('还未选中任何一件商品。');	
				return false;
			} else {
				window.location.href = te$.getUrl('orderConfirm') + '?orderItemStr=' + this.cartIds + '&_=' + (new Date()).valueOf();
			}
		},

		/*order action*/
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
			$('#btnCheckout').bind(clickAndTouch, function(){
				if (t$('userMessage').value.length > 0) {
					t$('userComment').value = t$('userMessage').value;
				}

				if (checkConfirm()) 
					t$('orderInfo').submit()
				else {
					te$.ui.msgBox.show('请填写好收货信息。');
				}
			});

			$('#btnShowAllAddress').bind(clickAndTouch, function(){
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
				var	country,
					province,
					city,
					oldId = t$('addressId').value;

				if (this.value == oldId) {
					this.checked = true;
					t$('countryModify').value = this.getAttribute('data-country');
					t$('provinceModify').value = this.getAttribute('data-province');
					t$('cityModify').value = this.getAttribute('data-city');
				}
			})
			.bind(clickAndTouch, function(){
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

			$('.address_item').bind(clickAndTouch, function(){
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
					$(this).bind(clickAndTouch, function() {
						coupon.useCoupon(code, true);
					});
					$(this).html('取消');
				} 
				else {
					$(this).bind(clickAndTouch, function() {
						coupon.useCoupon(code);
					});
				}
			});

			//input coupon code
			$('#btnUseCoupon').bind(clickAndTouch, function() {
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
					this.addEventListener(clickAndTouch, function(){
						var	code = this.getAttribute('data-coupon-code'),
							action = this.getAttribute('data-action') == 'delete' ? true : false;

						coupon.useCoupon(code, action);
					});
				}
			});		

			address.fillCheckoutAddress();
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

	favorite = {
		initFavorite : function() {
			$('.link_del_favorite_item').each(function(){
				$(this).bind(clickAndTouch, function(){
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
							if ($('.favorite_item').length < 1) {
								$('#myFavorite').append('<div class="blank_tip"><b class="blank_tip_no">no</b>收藏夹空啦：（</div>');
								//$('#myFavorite').remove();								
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
	};

	return {
		cart : cart,
		address : address,
		pay : pay,
		favorite : favorite
	}
})(window, te$, $);

te$.mobile.getUserScore = function() {
	$.ajax({
		url : te$.getUrl('userScore'),
		dataType : 'jsonp',
		cache : false,
		data : {'action' : 'userScore'},
		jsonpCallback : 'trendyCall',
		complete : function() {
			te$.mobile.getUserVip();
		}
	});
};

te$.mobile.getUserVip = function() {
	$.ajax({
		url : te$.getUrl('vipStatus'),
		dataType : 'jsonp',
		cache : false,
		data : {'action' : 'userVipStatus'},
		jsonpCallback : 'trendyCall'
	});
};

te$.mobile.homeInfo = function() {
	if (window.navigator.userAgent.toLowerCase().indexOf('micromessenger') >= 0) {
		$('.mc_addition').find('ul').eq($('.mc_addition').find('ul').size() - 1).hide();
	}
	
	t$('userQrCode').onclick = function() {
		t$('userQrCodePic').style.webkitTransform = 'scale(0)';
		t$('userQrCodePic').style.transform = 'scale(0)';
		this.style.visibility = 'hidden';
	}
	t$('btnShowQrCode').onclick = function() {
		t$('userQrCode').style.visibility = 'visible';
		t$('userQrCodePic').style.webkitTransform = 'scale(1, 1)';
		t$('userQrCodePic').style.transform = 'scale(1, 1)';
	}
	t$('userName').innerHTML = te$.cookie('suli');
	t$('memberName').innerHTML = te$.cookie('suli');
	this.getUserScore();
};

te$.mobile.showUserScore = function(o) {
	var c;
	if (window.location.href.indexOf('/user/Home.do') >= 0) {
		if ('code' in o && o.code == 1) {
			c = o.content;
			if (c.userProfileBeanList.length > 0) {
				if (t$('txUserScore')) t$('txUserScore').innerHTML = parseInt(c.userProfileBeanList[0].userScore);
				// if (c.userProfileBeanList[0].vipStatus != 0) {
				// 	if (t$('txUserVipLevel')) {
				// 		t$('txUserVipLevel').innerHTML = 'vip会员' + (c.userProfileBeanList[0].membershipCard ? '：' + c.userProfileBeanList[0].membershipCard : '');
				// 	}
				// 	if (t$('linkBindVip')) t$('linkBindVip').style.display = 'none';
				// }						
			}
			else {
				if (t$('txUserScore')) t$('txUserScore').innerHTML = 0;
				// if (t$('txUserVipLevel')) {
				// 	t$('txUserVipLevel').innerHTML = '非VIP会员';
				// }
			}

			if (t$('txProgress')) {
				t$('txProgress').getElementsByTagName('span')[0].style.width = c.userInfoCompleted + '%';
				t$('txProgress').getElementsByTagName('b')[0].innerHTML = parseInt(c.userInfoCompleted) + '%'
			}
		}
	}
};

te$.mobile.showVipStatus = function(o) {
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
};