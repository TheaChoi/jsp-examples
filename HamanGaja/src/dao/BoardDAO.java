package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import dto.BoardDTOIn;
import dto.ListDTOOut;
import dto.pageDTOIn;

public class BoardDAO {
	
	public boolean write(BoardDTOIn dto) throws SQLException{
		PreparedStatement pstm;
		ResultSet rs;
		
		Connection con = DBCP.getConnection();
		String sql = "insert into board set type=?, title=?, content=?, photo=?, map=?";
		String map="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d51009.071032223575!2d128.2999384089632!3d36.96045809232604!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3563f4913b29f6d7%3A0x20aafaf95cba675b!2z64-E64u07IK867SJ!5e0!3m2!1sko!2skr!4v1496277584007";
		pstm = con.prepareStatement(sql);
		pstm.setString(1, dto.getType());
		pstm.setString(2, dto.getTitle());
		pstm.setString(3, dto.getContent());
		pstm.setString(4, dto.getPhoto());
		pstm.setString(5, map);
		
		int ret = pstm.executeUpdate();
		pstm.close();
		con.close();
		if(ret==1){
			return true;
			
		}else{
			return false;
		}
	}
	
	
	public ArrayList<ListDTOOut> listAll(pageDTOIn page, String type) throws SQLException {
		
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		int start = pageNo * pageSize;  //시작글의 위치
		int mode = page.getMode(); //-1검색어 미사용, 0이면 검색어 사용 리스트 만든다
		
		Connection con = DBCP.getConnection();
		PreparedStatement pstm;
		ResultSet rs;
		
		ArrayList<ListDTOOut> list = new ArrayList<ListDTOOut>();
		
		if(mode!=0)
		{
			String sql = "select num, title, reg_date, count from board where type=? order by num desc limit ?,?";
			pstm = con.prepareStatement(sql);
			pstm.setString(1,type);
			pstm.setInt(2, start);
			pstm.setInt(3, pageSize);
			rs = pstm.executeQuery();
			
		}else{
			//검색어를 사용하는 sql 문장 작성
			String sql = "select num, title, reg_date, count from board where type=? and title like ? order by num desc limit ?,?";  //이 안에 %쓰지 말고
			String word = "%"+page.getWord()+"%";  
			pstm = con.prepareStatement(sql);
			pstm.setString(1,type);
			pstm.setString(2, word);
			pstm.setInt(3, start);
			pstm.setInt(4, pageSize);
			rs = pstm.executeQuery();
			
		}
		while(rs.next()==true){
			int num = rs.getInt("num");
			String title = rs.getString("title");
			String reg_date = rs.getString("reg_date");
			int count = rs.getInt("count");
			
			
			//각 글목록을 객체에 저장함
			ListDTOOut dto = new ListDTOOut(num, title, reg_date, count, type);
			
			//글목록을 저장한 객체를 리스트배열에 추가함
			list.add(dto);
		}
		rs.close();
		pstm.close();
		con.close();
		return list;
	}
	
	public int countAll(String type, pageDTOIn page) throws SQLException{
		
		Connection con = DBCP.getConnection();
		PreparedStatement pstm;
		ResultSet rs;
		int mode = page.getMode();
		
		if(mode==-1){ //검색어를 이용하지 않는경우
			 String sql  = "select count(*) from board where type=?";
		      pstm = con.prepareStatement(sql);
		      pstm.setString(1, type);
		}else{ //검색어 사용함
			 String sql  = "select count(*) from board where type=? and title like ?";
			 String word = "%"+page.getWord()+"%";
		      pstm = con.prepareStatement(sql);
		      pstm.setString(1, type);
		      pstm.setString(2, word);
		}
	
	      rs = pstm.executeQuery();
	      
	      rs.next();
	      int count = rs.getInt("count(*)");
	      System.out.println(type+"게시판 글개수:"+count);
	      rs.close();
		pstm.close();
		con.close();
		return count;
		
	}
	
	

}
