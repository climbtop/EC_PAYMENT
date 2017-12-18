/**
 * Created by xql on 2015/11/30.
 */
testSize();
$(function(){
    testSize();
    indexImg();
    payChoose();
    sildeBottom();
    addMin();
    setTotal();
    sizePrize();
    clearIput();
    detailNav();
    setTimeout('navWidth()',100);

});

$(window).resize(function(){
    testSize();
});

function navWidth() {
    var window_w = $(window).width();
    if(window_w>640) {
        window_w=640;
    }
    else if(window_w<320) {
        window_w=320
    }
    $(".swiper-pagination-bullet").css("width",window_w/3);
}
function testSize(){   //字体自适应
    var window_w = $(window).width();
    var initialW = 320;
    var initialFont = 0.625;
    if(window_w<initialW){
        $('html').css('font-size',60+'%');
    }else if(window_w<=initialW*2){
        $('html').css('font-size',window_w/(initialW/initialFont)*100+'%');
    }else{
        $('html').css('font-size',2*initialW/(initialW/initialFont)*100+'%');
    }
}
function indexImg(){ //首页轮播图
    var wid = $(window).width();
    if(wid>640) {
        wid=640;
    }
    else if(wid<320) {
        wid=320
    }
    $('#show,#show .img,#show .img span a img').css('width',wid);
    var s_1l=$(".show1 span img").length;
    var s_2l=$(".show2 span img").length;
    $('.show2 .img span').css('width',wid*(parseInt(s_2l)+1));
    $('.show2 .img span a img').css('min-height',wid);
    $('.show1 .img span').css('width',wid*(parseInt(s_1l)+1));
    $('.show1 .img span a img').css('min-height',wid/2);
    var img_height1=$('.show1 .img span a img').height();
    var img_height2=$('.show2 .img span a img').height();
    $('.show1').css('height',img_height1);
    $('.show2').css('height',img_height2);
}

function payChoose() {  //支付方式的打勾选择
     var len=$(".radio_pay").length
    $(".radio_pay").change(function(){

    for(var i=0;i<len;i++){
       if( $(".radio_pay").eq(i).prop("checked")==true)
       {$(".radio_pay").eq(i).siblings(".chose_img").removeClass("no_choose").addClass("choose") }
        else{$(".radio_pay").eq(i).siblings(".chose_img").removeClass("choose").addClass("no_choose") }
        }
     })
    }

function sildeBottom(){ //配送时间弹出
    $("#send_time").on("click",function(){
        var dis=$(".big_canvas").css("display");
        if(dis=="none"){
            $("html").css("overflow","hidden");
            $("body").css("overflow","hidden");
            $(".big_canvas").css("display","block");
            $(".s_time").slideDown("fast");
        }
    })
    $(".big_canvas").on("click",function(){
        $("html").css("overflow","");
        $("body").css("overflow","");
        $(".big_canvas").css("display","none");
        $(".s_time").slideUp("fast");
    })
}
function addMin(){ //购物车增减
    $(".add").on("click",function(){
        var num_text=$(this).siblings(".num_m");
        num_text.text(parseInt(num_text.text())+1);
        setTotal();
    })
    $(".min").on("click",function(){
        var num_text=$(this).siblings(".num_m");
        num_text.text(parseInt(num_text.text())-1);
        if(parseInt(num_text.text())<1){
            num_text.text(1);
        }
        setTotal();
    })
}

function setTotal(){ //购物车总价格
    var tatal=0;
    $(".order_num").each(function(){
      var l_prize=parseFloat($(this).find(".list_prize").text().replace(/^￥/,''));
        var num_shop=parseInt($(this).find(".num_m").text());
         tatal+=l_prize*num_shop;
    })
    $("#allprize").text("￥"+tatal.toFixed(2));
}
function boxShow(){   //弹出框的出现
    $("html").css("overflow","hidden");
    $("body").css("overflow","hidden");
    $(".all_canvas").show();
}
function boxShow1(){   //弹出框的出现
    $("html").css("overflow","hidden");
    $("body").css("overflow","hidden");
    $(".all_canvas1").show();
}
function hide_tip(){   //点击弹出框的确定,隐藏
    $("html").css("overflow","");
    $("body").css("overflow","");
    $(".all_canvas,.all_canvas1").hide();
}
function sizePrize(){ // 显示价格的不同文字大小
    $(".shop_prize").each(function(){
      var p_text=$(this).text();
        var prize1="<span style='font-size: 1.2rem;'>"+p_text.substr(0,1)+"</span>";
        var prize2=p_text.substr(1,p_text.lastIndexOf("."));
        var prize3="<span style='font-size: 1.7rem;'>"+p_text.substr(-2,2)+"</span>";
        $(this).html(prize1+prize2+prize3);
    })
}
function clearIput() {  //登录注册判断符合条件
    $("#cancel").on('click', function (){
        $("#tel_input").val("");
    })
    $("#tel_input").on("blur", function () {
        var myreg = /^1[3458]\d{9}$/;
        var tel_num = $(this).val();
        if (!myreg.exec(tel_num)) {
            $(this).parent(".land_input").siblings(".code_wrong").show();
            $(this).siblings("label").removeClass("c_3").addClass("c_8");
        }
    })
    $("#tel_input").focus(function(){
        $(this).parent(".land_input").siblings(".code_wrong").hide();
        $(this).siblings("label").removeClass("c_8").addClass("c_3");
    })
}

function detailNav(){
    $(".content_nav").on("click",function(){
        $("#iframe1").show();
        $("#iframe2").hide();
        $(this).addClass("c_2");
        $(".comment_nav").removeClass("c_2");
    })
    $(".comment_nav").on("click",function(){
        $("#iframe2").show();
        $("#iframe1").hide();
        $(this).addClass("c_2");
        $(".content_nav").removeClass("c_2");
    })

}