

function f1(a,b) {
	dist_wise=true;
	   myFunc(a,b);
}//f1

function myAlert( msg) {
	 
		 alert(msg);
	 
}//myAlert


//onClick event funtion
function myFunc(evt, item,val,name) {
	
	var msg;
	var fieldkey;
	 try{
		msg=item[0]._chart.data.datasets[0].labels[item[0]._index];
		fieldkey=document.getElementById(evt.target.id).getAttribute('fieldkeyword');
		}
	 catch(err){
		 
		 try{
		 msg=item[0]._view.label;
		 }
		 catch(err){
			
				 msg=dist_name[item[0]._index];
			 
			 }
		 }
	 
	 $("#alertMsg").append(' <div id="alert'+item[0]._index+'"    style=" width: 98px; height: 45px; padding-top: 3px;float:left	" class="alert alert-info">'+ ' <a href="" onclick=false;  class="close" data-dismiss="alert" aria-label="close">&times;</a>'
			   +'<div onclick=myAlert("'+msg+'") class="alert alert-info"><strong>'+ msg   +'</strong></div> </div>');
  
	//  msg=name+","+msg
   //ajax
   ajaxCall(msg,fieldkey);
   
}// myFunc  function




