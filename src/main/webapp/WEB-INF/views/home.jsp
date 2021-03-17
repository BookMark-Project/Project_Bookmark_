<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>책갈피</title>
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
 <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
 <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
  <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <link href="css/simple-sidebar.css" rel="stylesheet">
  <link href="css/home.css" rel="stylesheet">

<!-- sidebar -->
<link rel="stylesheet" type="text/css" href="./css/simple-sidebar.css">
<script type="text/javascript" src="./js/sidebar.js"></script> 
<script src="https://code.jquery.com/jquery-latest.js"></script>
<script>
$(document).ready(function(){
  $("#myBtn").click(function(){
    $("#myModal").modal("toggle");
  });

});
</script>
</head>
<body>
<div id="main">
	<div id="header">    
	<div id="mySidebar" class="sidebar">
	<div class="sidebar-header">
	
		<h3>당신의 책갈피</h3>
	</div>

	<p>User1님이 로그인 중 입니다.</p>
	<a href="./home.do">Home</a>
	<a href="./mypage.do">My Page</a>
	<a href="./list.do">모든 게시글 보기</a>
	<a href="./book_list.do">책 구경하기</a>
	
	<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal" 
	>
    새로운 글 작성하기
  </button>
  
 <!-- The Modal -->
  <div class="modal" id="myModal">
    <div class="modal-dialog">
      <div class="modal-content">
      
      
        <!-- Modal Header -->
        <div class="modal-header">
          <h4 class="modal-title">글 작성하기</h4>
          <button type="button" class="close" data-dismiss="modal">&times;</button>
        </div>
        
        <!-- Modal body -->
        <div class="modal-body">
        	<h6>작성자 : <input type="text" /></h6>
        	<h6>제목 : <input type="text" /></h6>
        	<h6>이미지: <input type="file" name="fileupload" accept="image/*" />
			        	<input type="file" name="fileupload" accept="image/*" />
			        	<input type="file" name="fileupload" accept="image/*" /></h6>
			<h6>책선정: <input type="text" />
			<button type="button" >책검색</button></h6>
			          
        </div>
        
        <!-- Modal footer -->
        <div class="modal-footer">
        <button type="button" class="btn btn-submit" data-update="modal">업로드 하기</button>
          <button type="button" class="btn btn-danger" data-dismiss="modal">닫기</button>
        </div>
        
        
      </div>
    </div>
  </div>


	</div>
    </div>
 </div>

	
		<p>
			<span>
				<button class="sidebar-btn" onclick="sidebarCollapse()">
					<span><i class="fa fa-bars" aria-hidden="true"></i></span>
	             </button>
			</span>
	        <span><a class="navbar-brand" href="./home.do"> <img src="./images/logo.png" alt="logo" style="width: 100px;"></a></span>
	        <span><a class="button" href="./login.do">start</a></span>
			<span><a class="button1" href="./search.do" ><i class="fa fa-search" aria-hidden="true"></i></a></span>	
				
    	</p>
     
    <div class="container">
			  <h1>생각을 보고, 나눔에 감동하다. 책갈피</h1>
			  <p>책에 대한 감성과 느낌을 담아.</p> 
			  <p>책갈피에 넣어두세요.</p>
			  <p>공유하며 더욱 큰 감동을 느끼세요 .</p>
	</div>
     
  
        
   <!-- 이달의 게시글  -->
    	<div id="content">
    	<div></div>
         <table>
         	<tr rowspan="3">
         	
         	</tr>
         	<tr>
         	</tr>
         	<tr>
         	</tr>
         </table>
      </div>
 </body>
 </html>