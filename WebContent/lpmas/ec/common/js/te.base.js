/*document.createElement替代*/
te$.c$ = function(a) {
	return document.createElement(a)		
};

te$.detectMobile = function() {
	var agent = navigator.userAgent || navigator.vendor || window.opera;
	if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(agent)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(agent.substr(0,4)))
		return true
	else return false;
};

/*去字符串首尾空格*/
te$.trim = function(h) {
	try {
		return h.replace(/^\s+|\s+$/g, "")
	} catch(j) {
		return h
	}
};

te$.hasClass = function(ele,cls) { 
	return ele.className.match(new RegExp('(\\s|^)'+cls+'(\\s|$)')); 
};

te$.addClass = function(ele,cls) { 
	if (!te$.hasClass(ele,cls)) ele.className += " "+cls; 
}; 

te$.removeClass = function(ele,cls) { 
	if (te$.hasClass(ele,cls)) { 
	var reg = new RegExp('(\\s|^)'+cls+'(\\s|$)'); 
	ele.className=ele.className.replace(reg,' '); 
	} 
}; 

te$.getByClass = function(p, j, m) {
	//p: class, j: element, m: container
	p = te$.trim(p);
	j = j || "*";
	if (!m) {
		return []
	}

	var	n = [],
		q = m.getElementsByTagName(j);

	for (var o = 0, h = q.length; o < h; ++o) {
		if (te$.hasClass(q[o], p)) {
			n[n.length] = q[o];
		}
	}
	return n
};

//模板批量替换{...}内容
te$.subStitute = function(str, obj) {
	if (!(Object.prototype.toString.call(str) === '[object String]')) {
		return '';
	}

	// {}; new Object(), new Class()
	// Object.prototype.toString.call(node=document.getElementById("xx")) : ie678 == '[object Object]', other =='[object HTMLElement]'
	// 'isPrototypeOf' in node : ie678 === false , other === true
	if(!(Object.prototype.toString.call(obj) === '[object Object]' && 'isPrototypeOf' in obj)) {
		return str;
	}

	// https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/String/replace
	return str.replace(/\{([^{}]+)\}/g, function(match, key) {
		var value = obj[key];
		return ( value !== undefined) ? ''+value :'';
	});
};

te$.getQueryParam = function(queryName) {
	if (!queryName) return '';

	var  reg = new RegExp("(^|&)"+ queryName +"=([^&]*)(&|$)");     
	var  r = window.location.search.substr(1).match(reg);
	if (r!=null) return r[2];
	return '';  		
};

//获取尺码描述，如sizeList中未定义则原样输出
te$.gloGetSize = function(s) {
	if (!s) return null;
	if (s in te$.sizeList) return te$.sizeList[s]
	else return s;
};

te$.formatMoney = function(n, symbol, thousand) {
	symbol = symbol !== undefined ? symbol : '';
	thousand = thousand !== undefined ? thousand : '';		
	var	place = 2,
		decimal = '.',
		negative = n < 0 ? '-' : '',
		i = parseInt(n = Math.abs(+n || 0).toFixed(place), 10) + '',
		j = (j = i.length) > 3 ? j % 3 : 0;
	return symbol + negative + (j ? i.substr(0, j) + thousand : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, '$1' + thousand) + (place ? decimal + Math.abs(n - i).toFixed(place).slice(2) : '');
};

te$.codeToBrand = function(code) {
	var	codeMap = {
			'1' : 'ochirly',
			'2' : 'fiveplus',
			'3' : 'trendiano',
			'c' : 'covengarden'
		}

	if (code.toLowerCase() in codeMap) return codeMap[code.toLowerCase()]
	else return code;
};

te$.codeToYear = function(code) {
	var	yearMap = {
			'y' : '2015',
			'h' : '2016',
			'j' : '2017',
			'g' : '2018',
			'z' : '2019',
			'r' : '2020',
			'n' : '2021',
			'w' : '2022',
			't' : '2023',
			'l' : '2024',
			's' : '2025',
			'm' : '2026'
		}

	if (code.toLowerCase().substr(0, 1) in yearMap) return yearMap[code.toLowerCase().substr(0, 1)]
	else return '20' + code;
};

te$.codeToRange = function(code) {
	return String.fromCharCode(96 + parseInt(code));
};

te$.skuToImgUrl = function(sku, imgType, num) {
	var	imgBaseUrl = '//img1.ochirly.com.cn/wcsstore/TrendyCatalogAssetStore/images/trendy/',
		brand = te$.codeToBrand(sku.substr(0, 1)),
		year = te$.codeToYear(sku.substr(1, 2)),
		range = te$.codeToRange(sku.substr(3, 1));

	return imgBaseUrl + brand + '/' + year + '/' + range + '/' + sku + '/' + sku + '_' + imgType + '_' + num + '.jpg';
};

te$.getSizeDesc = function(size) {
	if (size in te$.sizeList) return te$.sizeList[size]
	else return size;
};

te$.showWashedImg = function(id) {
	var	d,
		s = null,
		h = '';
	if (!id) return;

	d = t$(id);
	if (d) {
		s = d.innerHTML.split('\n');
		for (var i = 0, l = s.length; i < l; i++) {
			if (s[i] in te$.wash[te$.getCurrBrand()]) {
				h += '<img src="' + te$.wash[te$.getCurrBrand()][s[i]] + '" />';	
			}
		}
	}

	d.innerHTML = h;
};

te$.point = (function(window, te$, $, undefined){
	var
	setPointExchangeBtn = function(btns) {
		btns.bind('click', function(){
			var p = this.getAttribute('data-point');
			if (!p) return;

			pointExchange(p);
		});
	},

	pointExchange = function(id) {
		$.ajax({
			url : te$.getUrl('pointExchange'),
			dataType : 'jsonp',
			cache : false,
			data : {'productItemId':id, 'action' : 'pointExchange'},
			jsonpCallback : 'trendyCall'
		});
	};

	return {
		'setPointExchangeBtn' : setPointExchangeBtn
	}
})(window, te$, $);