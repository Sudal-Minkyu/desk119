$(function() {

    $(document).on("click","#checkSuccessBtn",function(){
        startDel($("#delId").val(),true)
    });
    $(document).on("click","#checkCancelBtn",function(){
        startDel($("#delId").val(),false)
    });

    $(document).on("click","#mobileSuccessBtn",function(){
        mobileDelStart($("#delId").val(),true)
    });
    $(document).on("click","#mobileCancelBtn",function(){
        mobileDelStart($("#delId").val(),false)
    });

    $(document).on("click","#collectionSuccessBtn",function(){
        continueSaveCheck()
    });
    $(document).on("click","#checkYesBtn",function(){
        startYesorNo(true)
    });
    $(document).on("click","#checkNoBtn",function(){
        startYesorNo(false)
    });
    $(document).on("click","#successBtn",function(){
        $('#popupId').remove();
    });
    $(document).on("click","#successBtn2",function(){
        $('#popupId').remove();
        return location.href = "/login";
    });
    $(document).on("click","#successBtnReg",function(){
        $('#popupId').remove();
        window.location.href = "/record/list";
    });
    $(document).on("click","#successBtn3",function(){
        $('#popupId').remove();
        return  window.location.reload();
    });
    $(document).on("click","#successBtnSign",function(){
        $('#popupId').remove();
        return  window.location.href ="/";
    });
});


function alertSuccess(text,num) { //성공창(삭제성공시),저장성공시

    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat success"></div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    if(num===1){
        html +='<button id="successBtnReg" class="popup__btn popup__btn--success">확인</button>';
    }else if(num===2){
        html +='<button id="successBtnSign" class="popup__btn popup__btn--success">확인</button>';
    }else{
        html +='<button id="successBtn" class="popup__btn popup__btn--success">확인</button>';
    }

    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);

}

function alertCancel(text,num) { //에러창(로그인만료),오류

    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat cancel"></div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    if(num===1){
        html +='<button id="successBtn2" class="popup__btn popup__btn--success">확인</button>';
    }else if(num===2) {
        html +='<button id="successBtn3" class="popup__btn popup__btn--success">확인</button>';
    }else{
        html +='<button id="successBtn" class="popup__btn popup__btn--success">확인</button>';
    }
    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);

}

function alertCaution(text) { //경고창
    var cau = "!";

    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat caution">'+cau+'</div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    html +='<button id="successBtn" class="popup__btn popup__btn--success">확인</button>';
    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);

}

function alertCaution2(text) { // 창이 좀더 긴 경고창
    var cau = "!";

    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box2">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat caution">'+cau+'</div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    html +='<button id="successBtn" class="popup__btn popup__btn--success">확인</button>';
    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);

}

// 삭제 알림창.
function alertCheck(text,id,num) { //정말삭제할껀지확인하는창
    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat check"></div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    html +='<input type="hidden" id="delId" value="'+id+'" />';
    if(num!==1) {
        html += '<button id="checkSuccessBtn" class="popup__btn popup__btn--success">확인</button>';
        html += '<button id="checkCancelBtn" class="popup__btn popup__btn--cancel">취소</button>';
    }else{
        html += '<button id="mobileSuccessBtn" class="popup__btn popup__btn--success">확인</button>';
        html += '<button id="mobileCancelBtn" class="popup__btn popup__btn--cancel">취소</button>';
    }
    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);
}

//수거업무저장 전용
function alertCollectionSuccess(text) { //성공창(삭제성공시),저장성공시

    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat success"></div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    html +='<button id="collectionSuccessBtn" class="popup__btn popup__btn--success">확인</button>';
    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);

}

//여무붇기
function alertSaveCheck(text,id) { //정말삭제할껀지확인하는창
    var html = '';

    html +='<div id="popupId" class="popup popup--dim">';
    html +='<div class="popup__box">';
    html +='<div class="popup__content">';
    html +='<div class="popup__stat check"></div>';
    html +='<div class="popup__text">'+text+'</div>';
    html +='</div>';
    html +='<div class="popup__buttons">';
    html +='<input type="hidden" id="delId" value="'+id+'" />';
    html +='<button id="checkYesBtn" class="popup__btn popup__btn--success">예</button>';
    html +='<button id="checkNoBtn" class="popup__btn popup__btn--cancel">아니오</button>';
    html +='</div>';
    html +='</div>';
    html +='</div>';

    $('#alertpop').html(html);
}