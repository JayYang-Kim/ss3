package com.sp.customer.board;

import java.util.List;
import java.util.Map;

public interface BoardService {
	public int insertBoard(Board dto, String mode);
	public int dataCount(Map<String, Object> map);
	
	public List<Board> listBoard(Map<String, Object> map);
	
	public Board readBoard(int num);
	
	public Board preReadBoard(Map<String, Object> map);
	public Board nextReadBoard(Map<String, Object> map);
	
	public int updateHitCount(int num);
	
	public int updateBoard(Board dto);
	public int deleteBoard(int num);
}
