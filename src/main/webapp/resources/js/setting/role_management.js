 /* 
        권한관련 on/off 
        on : btn-info
        off : btn-secondary
    */

	
	        $(".btn-info").click(function() {
         if( $(this).hasClass("default")){
            return;
         }
         
         $(this).toggleClass("btn-info").toggleClass("btn-secondary").toggleClass("on").toggleClass("off");
         $(this).closest(".one-role-total-wrapper").removeAttr('data-state');
         $(this).closest(".one-role-total-wrapper").data('state','update'); 
         })
     
         $(".btn-secondary").click(function() {
         if( $(this).hasClass("default")){
            return;
         }
            $(this).toggleClass("btn-info").toggleClass("btn-secondary").toggleClass("off").toggleClass("on");
            $(this).closest(".one-role-total-wrapper").removeAttr('data-state');
         	$(this).closest(".one-role-total-wrapper").data('state','update'); 
            
         })
     
         /* 
             역할 삭제 
         */
         $(".delete-role").click(function() {
         if( $(this).hasClass("default")){
            return;
         }
         
         $(this).closest(".one-role-total-wrapper").removeAttr('data-state');
         $(this).closest(".one-role-total-wrapper").data('state','delete'); 
         $(this).closest(".one-role-total-wrapper").hide();
         })
     
         /* 
             역할 추가
         */
     
         let a;
     
        $(".add-role").click(function() {
            var input = $("#new-role-input").val();
            if(input != ""){
             console.dir(input);
             
     
             let b = $(".role-list-content-new").children().clone();
             b.find(".role-title-wrapper").children().attr('value' , input);
             b.appendTo(".role-list-wrapper");
             
     
           //역할삭제
            b.find("#role").children('.delete-role').on('click', e=> {
	
			if(b.closest(".one-role-total-wrapper").data('state') == 'new') {
				b.closest(".one-role-total-wrapper").remove();
				
			}else{
				b.closest(".one-role-total-wrapper").removeAttr('data-state');
	            b.closest(".one-role-total-wrapper").data('state','delete'); 
	            
	            b.hide();
			}

             })
     
     
     
             b.find("#role").children('.btn-info').click(function () {
                 $(this).toggleClass("btn-info").toggleClass("btn-secondary").toggleClass("on").toggleClass("off");
                 
                 if(b.closest(".one-role-total-wrapper").data('state') == 'none') {
	 				b.closest(".one-role-total-wrapper").removeAttr('data-state');
        		 	b.closest(".one-role-total-wrapper").data('state','update'); 
				}
                
             })
             
             b.find("#role").children('.btn-secondary').click(function () {
                 $(this).toggleClass("btn-info").toggleClass("btn-secondary").toggleClass("on").toggleClass("off");
                 
				 if(b.closest(".one-role-total-wrapper").data('state') == 'none') {
	 				b.closest(".one-role-total-wrapper").removeAttr('data-state');
        		 	b.closest(".one-role-total-wrapper").data('state','update'); 
				}
             })
     
     
     
             $("#new-role-input").val("");
     
            }else{
                alert("역할명을 입력해주세요");
            }
     
            
          
         
      
        })
     
         /* 설정 저장 모달창 */
         var firstSaveModal = new Modal("설정 저장","설정을 저장하시겠습니까?");
          
         var secondSaveModal = new Modal("저장 완료","저장이 완료되었습니다.");
         
         
     
         firstSaveModal.createTwoButtonModal("저장","취소"); //버튼 2개생성 first-button : 저장 second-button : 취소
         firstSaveModal.makeModalBtn($(".save"));   //버튼에 지정
     
     
         secondSaveModal.createAlertModal(); //두번쨰모달 생성
         secondSaveModal.makeModalBtn($(".first-button")); //first-button : 저장 <--여기에지정
         
	
	

	 

     
     
     
     
     
     
        
        