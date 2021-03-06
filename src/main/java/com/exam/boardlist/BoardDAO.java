package com.exam.boardlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class BoardDAO {

	@Autowired
	private DataSource dataSource;
	
	//board_list
	public ArrayList<BoardTO> boardList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<BoardTO> lists = new ArrayList<BoardTO>();
		
		try{
			conn = dataSource.getConnection();
			
			String sql = "select seq, date, title, useq, filename, filesize, content, bseq, hit, comment from board order by date desc";
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			
			rs = pstmt.executeQuery();		
			while( rs.next() ) {
				BoardTO to = new BoardTO();
				to.setSeq( rs.getString( "seq" ) );
				to.setDate( rs.getString( "date" ) );
				to.setTitle( rs.getString( "title" ) );
				to.setUseq( rs.getString( "useq" ) );
				to.setFilename( rs.getString( "filename" ) );
				to.setFilesize( rs.getString( "filesize" ) );
				to.setContent( rs.getString( "content" ) );
				to.setBseq( rs.getString( "bseq" ) );
				to.setHit( rs.getString( "hit" ) );
				to.setComment( rs.getString( "comment" ) );

				lists.add( to );
			}				
			
		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(rs!=null) try{rs.close();}catch(SQLException e) {}
			if(pstmt!=null) try{pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try{conn.close();}catch(SQLException e) {}
		}
		
		return lists;
	}
		
	// board_list__overloading (paging)
	/*
	BoardListTO = BoardPagingTO
	listTO = pagingTO
	*/
	public BoardPagingTO boardList(BoardPagingTO pagingTO){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 

		// paging
		int cpage = pagingTO.getCpage();
		int recordPerPage = pagingTO.getRecordPerPage();
		int blockPerPage = pagingTO.getBlockPerPage();
	
		try{
			conn = dataSource.getConnection();

			//String sql = "select seq, date, title, useq, filename, filesize, content, bseq, hit, comment from board order by date desc";
			String sql = "select bnunltable.seq, date, filename, title, bnunltable.useq, nickname, Lcount, count(comment.bseq) Ccount " + 
					"from (select bnutable.seq, date, filename, title, bnutable.useq, nickname, count(likey.bseq) Lcount " + 
					"from (select board.seq, date, board.filename, title, useq, nickname from board inner join user on board.useq = user.seq) bnutable " + 
					"left outer join likey on bnutable.seq = likey.bseq group by bnutable.seq) bnunltable " + 
					"left outer join comment on bnunltable.seq = comment.bseq group by bnunltable.seq order by date desc";
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			rs = pstmt.executeQuery();

			rs.afterLast();
			pagingTO.setTotalRecord(rs.getRow()-1);
			rs.beforeFirst();
				
			pagingTO.setTotalPage(((pagingTO.getTotalRecord()-1) / recordPerPage) + 1 );
			
			int skip = (cpage-1)*recordPerPage;
			if(skip != 0) rs.absolute(skip);
			
			//ArrayList<BoardTO> lists = new ArrayList<BoardTO>();
			ArrayList<JoinBULCTO> lists = new ArrayList<JoinBULCTO>();
				
			//seq, date, filename, title, useq, nickname, Lcount, Ccount 
			if(rs.next()) {
				for(int i=0; i<recordPerPage; i++){
					JoinBULCTO to = new JoinBULCTO();
					to.setSeq( rs.getString( "seq" ) );
					to.setDate( rs.getString( "date" ) );
					to.setFilename( rs.getString( "filename" ) );
					to.setTitle( rs.getString( "title" ) );
					to.setUseq( rs.getString( "useq" ) );
					to.setNickname( rs.getString( "nickname" ) );
					to.setLcount( rs.getString( "Lcount" ) );
					to.setCcount( rs.getString( "Ccount" ) );

					lists.add(to);
						
					// DB에 저장된 데이터가 더이상 없는 경우 아래 구문을 수행한다.
					if(!rs.next()){
						// 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)보다 적을 경우. 아래 for문을 수행한다.
						for(; i<recordPerPage-1; i++){
							JoinBULCTO to1 =new JoinBULCTO();
							lists.add(to1);
						}
						// 만약 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)일 경우 아무것도 안하고 그대로 종료.
						break;
					}
				}
			}
			
			pagingTO.setJoinbulcList(lists);
				
			pagingTO.setStartBlock(((cpage-1)/blockPerPage)*blockPerPage + 1);
			pagingTO.setEndBlock(((cpage-1)/blockPerPage)*blockPerPage + blockPerPage);
			if(pagingTO.getEndBlock()>=pagingTO.getTotalPage()) {
				pagingTO.setEndBlock(pagingTO.getTotalPage());
			}
				
		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(rs!=null) try{rs.close();} catch(SQLException e) {}
			if(pstmt!=null) try{pstmt.close();} catch(SQLException e) {}
			if(conn!=null) try{conn.close();} catch(SQLException e) {}
		}
		//System.out.println(pagingTO.getCpage());
		return pagingTO;
	}
	
	
	//boardDelete
	public int boardDelete(BoardTO to) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int flag = 0;

		try{
			conn = dataSource.getConnection();

			String sql = "delete from board where seq = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, to.getBseq());
			
			int result = pstmt.executeUpdate();
			if(result == 1){
				flag = 1;
			}

		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(pstmt!=null) try{pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try{conn.close();}catch(SQLException e) {}
		}

		return flag;
	}
	
	// search_tlist
	/*
	//BoardPagingTO 
	//boardList => searchTList
	//pagingTO => slpagingTO
	
	public BoardPagingTO searchTList(BoardPagingTO slpagingTO, String searchword){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 

		// paging
		int cpage = slpagingTO.getCpage();
		int recordPerPage = slpagingTO.getRecordPerPage();
		int blockPerPage = slpagingTO.getBlockPerPage();
		
		//ArrayList<BoardTO> lists = new ArrayList<BoardTO>();
		
		try{
			conn = dataSource.getConnection();
			
			// sql문 수정하기 ★★★
			//String sql = "select seq, date, title, useq, filename, filesize, content, bseq, hit, comment from board limit 25, 45";
			String sql = "select seq, date, title, useq, filename, filesize, content, bseq, hit, comment from board where title like ? order by date desc";
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, "%"+searchword+"%");

			rs = pstmt.executeQuery();

			rs.afterLast();
			slpagingTO.setTotalRecord(rs.getRow()-1);
			rs.beforeFirst();
			
			slpagingTO.setTotalPage(((slpagingTO.getTotalRecord()-1) / recordPerPage) + 1 );
			
			int skip = (cpage-1)*recordPerPage;
			if(skip != 0) rs.absolute(skip);
			
			ArrayList<BoardTO> lists = new ArrayList<BoardTO>();
			
			//seq, date, filename, title, useq, nickname, Lcount, Ccount
			if(rs.next()) {
				for(int i=0; i<recordPerPage; i++){
					BoardTO to = new BoardTO();
					to.setSeq( rs.getString( "seq" ) );
					to.setDate( rs.getString( "date" ) );
					to.setTitle( rs.getString( "title" ) );
					to.setUseq( rs.getString( "useq" ) );
					to.setFilename( rs.getString( "filename" ) );
					to.setFilesize( rs.getString( "filesize" ) );
					to.setContent( rs.getString( "content" ) );
					to.setBseq( rs.getString( "bseq" ) );
					to.setHit( rs.getString( "hit" ) );
					to.setComment( rs.getString( "comment" ) );

					lists.add(to);
					
					
					// DB에 저장된 데이터가 더이상 없는 경우 아래 구문을 수행한다.
					if(!rs.next()){
						// 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)보다 적을 경우. 아래 for문을 수행한다.
						for(; i<recordPerPage-1; i++){
							BoardTO to1 =new BoardTO();
							lists.add(to1);
						}
						// 만약 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)일 경우 아무것도 안하고 그대로 종료.
						break;
					}
					
				}
			}
			
			slpagingTO.setBoardList(lists);
			
			slpagingTO.setStartBlock(((cpage-1)/blockPerPage)*blockPerPage + 1);
			slpagingTO.setEndBlock(((cpage-1)/blockPerPage)*blockPerPage + blockPerPage);
			if(slpagingTO.getEndBlock()>=slpagingTO.getTotalPage()) {
				slpagingTO.setEndBlock(slpagingTO.getTotalPage());
			}
			
		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(rs!=null) try{rs.close();} catch(SQLException e) {}
			if(pstmt!=null) try{pstmt.close();} catch(SQLException e) {}
			if(conn!=null) try{conn.close();} catch(SQLException e) {}
		}
		//System.out.println(slpagingTO.getCpage());
		return slpagingTO;
	}
	*/
	
	// search_tlist
	/*
	BoardPagingTO 
	boardList => searchTList
	pagingTO => slpagingTO
	*/
	public BoardPagingTO searchTList(BoardPagingTO slpagingTO, String searchword){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 

		// paging
		int cpage = slpagingTO.getCpage();
		int recordPerPage = slpagingTO.getRecordPerPage();
		int blockPerPage = slpagingTO.getBlockPerPage();
		
		//ArrayList<BoardTO> lists = new ArrayList<BoardTO>();
		
		try{
			conn = dataSource.getConnection();
			
			// sql문 수정하기 ★★★
			//String sql = "select seq, date, title, useq, filename, filesize, content, bseq, hit, comment from board where title like ? order by date desc";
			String sql = "select bnunltable.seq, date, filename, title, bnunltable.useq, nickname, Lcount, count(comment.bseq) Ccount " + 
					"from (select bnutable.seq, date, filename, title, bnutable.useq, nickname, count(likey.bseq) Lcount " + 
					"from (select board.seq, date, board.filename, title, useq, nickname from board inner join user on board.useq = user.seq) bnutable " + 
					"left outer join likey on bnutable.seq = likey.bseq group by bnutable.seq) bnunltable " + 
					"left outer join comment on bnunltable.seq = comment.bseq where title like ? group by bnunltable.seq order by date desc";
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, "%"+searchword+"%");

			rs = pstmt.executeQuery();

			rs.afterLast();
			slpagingTO.setTotalRecord(rs.getRow()-1);
			rs.beforeFirst();
			
			slpagingTO.setTotalPage(((slpagingTO.getTotalRecord()-1) / recordPerPage) + 1 );
			
			int skip = (cpage-1)*recordPerPage;
			if(skip != 0) rs.absolute(skip);
			
			ArrayList<JoinBULCTO> lists = new ArrayList<JoinBULCTO>();
			
			//seq, date, filename, title, useq, nickname, Lcount, Ccount
			if(rs.next()) {
				for(int i=0; i<recordPerPage; i++){
					JoinBULCTO to = new JoinBULCTO();
					to.setSeq( rs.getString( "seq" ) );
					to.setDate( rs.getString( "date" ) );
					to.setFilename( rs.getString( "filename" ) );
					to.setTitle( rs.getString( "title" ) );
					to.setUseq( rs.getString( "useq" ) );
					to.setNickname( rs.getString( "nickname" ) );
					to.setLcount( rs.getString( "Lcount" ) );
					to.setCcount( rs.getString( "Ccount" ) );

					lists.add(to);
					
					// DB에 저장된 데이터가 더이상 없는 경우 아래 구문을 수행한다.
					if(!rs.next()){
						// 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)보다 적을 경우. 아래 for문을 수행한다.
						for(; i<recordPerPage-1; i++){
							JoinBULCTO to1 =new JoinBULCTO();
							lists.add(to1);
						}
						// 만약 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)일 경우 아무것도 안하고 그대로 종료.
						break;
					}
					
				}
			}
			
			slpagingTO.setJoinbulcList(lists);
			
			slpagingTO.setStartBlock(((cpage-1)/blockPerPage)*blockPerPage + 1);
			slpagingTO.setEndBlock(((cpage-1)/blockPerPage)*blockPerPage + blockPerPage);
			if(slpagingTO.getEndBlock()>=slpagingTO.getTotalPage()) {
				slpagingTO.setEndBlock(slpagingTO.getTotalPage());
			}
			
		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(rs!=null) try{rs.close();} catch(SQLException e) {}
			if(pstmt!=null) try{pstmt.close();} catch(SQLException e) {}
			if(conn!=null) try{conn.close();} catch(SQLException e) {}
		}
		//System.out.println(slpagingTO.getCpage());
		return slpagingTO;
	}
	
	
	// search_nnlist
	/*
	BoardPagingTO 
	boardList => searchNNList
	pagingTO => snnlpagingTO
	BoardTO => UserTO
	*/
	public BoardPagingTO searchNNList(BoardPagingTO snnlpagingTO, String searchword){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 

		// paging
		int cpage = snnlpagingTO.getCpage();
		int recordPerPage = snnlpagingTO.getRecordPerPage();
		int blockPerPage = snnlpagingTO.getBlockPerPage();
		
		//ArrayList<BoardTO> lists = new ArrayList<BoardTO>();
		
		try{
			conn = dataSource.getConnection();
			
			// sql문 수정하기 ★★★	
			
			//String sql = "select seq, id, nickname, mail, address, addresses from user where nickname like ?";
			String sql = "select user.seq useq, id, nickname, mail, keywords, introduction, profile_filename, sum(Lcount) Lcount, count(bnltable.useq) Bcount from user " + 
					"left outer join (select board.seq bseq, date, title, board.useq, count(likey.bseq) Lcount from board left outer join likey on board.seq = likey.bseq group by board.seq) bnltable " + 
					"on user.seq = bnltable.useq " + 
					"where nickname like ? " + 
					"group by user.seq";
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1, "%"+searchword+"%");

			rs = pstmt.executeQuery();

			rs.afterLast();
			snnlpagingTO.setTotalRecord(rs.getRow()-1);
			rs.beforeFirst();
			
			snnlpagingTO.setTotalPage(((snnlpagingTO.getTotalRecord()-1) / recordPerPage) + 1 );
			
			int skip = (cpage-1)*recordPerPage;
			if(skip != 0) rs.absolute(skip);
			
			ArrayList<JoinBLUTO> lists = new ArrayList<JoinBLUTO>();
			
			// useq, id, nickname, mail, keywords, introduction, profile_filename, sum(Lcount) Lcount, count(bnltable.useq) Bcount 가져오기.
			if(rs.next()) {
				for(int i=0; i<recordPerPage; i++){
					JoinBLUTO to = new JoinBLUTO();
					to.setUseq( rs.getString( "useq" ) );
					to.setId( rs.getString( "id" ) );
					to.setNickname( rs.getString( "nickname" ) );
					to.setMail( rs.getString( "mail" ) );
					to.setKeywords( rs.getString( "keywords" ) );
					to.setIntroduction( rs.getString( "introduction" ) );
					to.setProfile_filename( rs.getString( "profile_filename" ) );
					to.setLcount( rs.getString( "Lcount" ) );
					to.setBcount( rs.getString( "Bcount" ) );

					lists.add(to);
					
					// DB에 저장된 데이터가 더이상 없는 경우 아래 구문을 수행한다.
					if(!rs.next()){
						// 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)보다 적을 경우. 아래 for문을 수행한다.
						for(; i<recordPerPage-1; i++){
							JoinBLUTO to1 =new JoinBLUTO();
							lists.add(to1);
						}
						// 만약 한 페이지에 recordPerPage개(예를 들면 10개) 게시글을 넣기로 했는데 데이터가 recordPerPage개(10개)일 경우 아무것도 안하고 그대로 종료.
						break;
					}					
					
				}
			}
			
			snnlpagingTO.setJoinbluList(lists);
			
			snnlpagingTO.setStartBlock(((cpage-1)/blockPerPage)*blockPerPage + 1);
			snnlpagingTO.setEndBlock(((cpage-1)/blockPerPage)*blockPerPage + blockPerPage);
			if(snnlpagingTO.getEndBlock()>=snnlpagingTO.getTotalPage()) {
				snnlpagingTO.setEndBlock(snnlpagingTO.getTotalPage());
			}
			
		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(rs!=null) try{rs.close();} catch(SQLException e) {}
			if(pstmt!=null) try{pstmt.close();} catch(SQLException e) {}
			if(conn!=null) try{conn.close();} catch(SQLException e) {}
		}

		return snnlpagingTO;
	}
	
	//writeOk
	public int writeOk(BoardTO to) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int flag = 0;

		try{
			conn = dataSource.getConnection();

			String sql = "insert into board values(0, now(), ?, ?, ?, ?, ?, ?, 0, 0)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, to.getTitle());
			pstmt.setString(2, to.getUseq());
			pstmt.setString(3, to.getFilename());
			pstmt.setString(4, to.getFilesize());
			pstmt.setString(5, to.getContent());
			pstmt.setString(6, to.getBseq());

			int result = pstmt.executeUpdate();
			if(result == 1){
				flag = 1;
			}

		} catch(SQLException e){
			System.out.println("[에러] " + e.getMessage());
		} finally {
			if(pstmt!=null) try{pstmt.close();}catch(SQLException e) {}
			if(conn!=null) try{conn.close();}catch(SQLException e) {}
		}

		return flag;
	}	
	//마이페이지 보드 리스트 출력 by 예찬
		public ArrayList<MyPageTO> boardList_Mypage(String useq) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			ArrayList<MyPageTO> lists = new ArrayList<MyPageTO>();
			
			try{
				conn = dataSource.getConnection();
				String sql = "select bl.seq as seq, bl.title as title, bl.filename as filename, bl.likey as likey , count(bseq) as comment from (select b.seq as seq, b.title, b.filename, b.useq as useq, count(l.bseq) as likey from (select seq, title, filename, useq from board where useq=?) as b left outer join likey as l on b.seq = l.bseq group by b.seq) as bl left outer join comment as c on bl.seq = c.bseq group by bl.seq order by bl.seq desc";
				pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				pstmt.setString(1, useq);
				
				rs = pstmt.executeQuery();		
				while( rs.next() ) {
					MyPageTO to = new MyPageTO();
					to.setSeq( rs.getString( "seq" ) );
					to.setTitle( rs.getString( "title" ) );
					to.setFilename( rs.getString( "filename" ) );
					to.setLike(rs.getInt("likey"));
					to.setComment(rs.getInt("comment"));

					lists.add( to );
				}				
				
			} catch(SQLException e){
				System.out.println("[에러] " + e.getMessage());
			} finally {
				if(rs!=null) try{rs.close();}catch(SQLException e) {}
				if(pstmt!=null) try{pstmt.close();}catch(SQLException e) {}
				if(conn!=null) try{conn.close();}catch(SQLException e) {}
			}
			
			return lists;
		}
	
}
