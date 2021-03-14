package com.exam.booklist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.exam.paging.pagingTO;
@Repository
public class BookDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public ArrayList<BookTO> BooklistTemplate(){
		String sql = "select master_seq, isbn13, title, author, publisher, img_url, description, pub_date from book order by title limit 15";
		ArrayList<BookTO> lists = (ArrayList<BookTO>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<BookTO>(BookTO.class));
		return lists;
	}
	
	public BookTO Book_infoTemplate(String master_seq){
		String sql = "select master_seq, isbn13, title, author, publisher, img_url, description, pub_date from book where master_seq=?";
		BookTO book = (BookTO) jdbcTemplate.queryForObject(sql, new Object[]{master_seq}, new RowMapper<BookTO>() {
			public BookTO mapRow(ResultSet rs, int rowNum) throws SQLException{
				BookTO to = new BookTO();
				to.setMaster_seq(rs.getString("master_seq"));
				to.setIsbn13(rs.getString("isbn13"));
				to.setTitle(rs.getNString("title"));
				to.setAuthor(rs.getString("author"));
				to.setPublisher(rs.getNString("publisher"));
				to.setImg_url(rs.getString("img_url"));
				to.setDescription(rs.getString("description"));
				to.setPub_date(rs.getString("pub_date"));
				return to;
			}
		});
		//(BookTO) jdbcTemplate.query(sql, new BeanPropertyRowMapper(BookTO.class));
		return book;
	}
	
	public pagingTO pagingList(pagingTO booklistTO) {
		//paging
		int cpage = booklistTO.getCpage();
		int recordPerPage = booklistTO.getRecordPerPage();
		int blockPerPage = booklistTO.getBlockPerPage();
		
		pagingTO pagingTO = new pagingTO();
		pagingTO.setCpage(cpage);
		pagingTO.setRecordPerPage(recordPerPage);
		pagingTO.setBlockPerPage(blockPerPage);
		
		String queryTotalRecords = "select count(*) from book";
		int totalItems = jdbcTemplate.queryForObject(queryTotalRecords, Integer.class);
		pagingTO.setTotalrecord(totalItems);
		
		pagingTO.setTotalPage((totalItems/5)-1);
		
		String sql = "select master_seq, isbn13, title, author, publisher, img_url, description, pub_date from book order by title limit " + booklistTO.getRecordPerPage()+ " offset " + cpage* booklistTO.getRecordPerPage();
		ArrayList<BookTO> lists = (ArrayList<BookTO>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<BookTO>(BookTO.class));
		pagingTO.setBookList(lists);
		
		pagingTO.setStartBlock(((cpage-1)/blockPerPage) * blockPerPage + 1);
		pagingTO.setEndBlock(((cpage-1)/blockPerPage)* blockPerPage + blockPerPage );
		if(pagingTO.getEndBlock() >= pagingTO.getTotalPage()) {
			pagingTO.setEndBlock(pagingTO.getTotalPage());
		}
		
		return pagingTO;
	}
}