package com.sp.customer.board;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.common.MyUtil;
import com.sp.member.SessionInfo;

//질문과 답변
@Controller("customer.boardController")
public class BoardController {
	@Autowired
	private BoardService service;

	@Autowired
	private MyUtil myUtil;
	
	@RequestMapping(value="/customer/board/list")
	// @RequestMapping(value="/customer/{group}/list")
	public String list(
			// @PathVariable String group,
			@RequestParam(value="pageNo", defaultValue="1") int current_page,
			@RequestParam(defaultValue="all") String condition,
			@RequestParam(defaultValue="") String keyword,
			HttpServletRequest req,
			Model model	) throws Exception {
		
		int rows = 10;
		int total_page;
		int dataCount;

		if (req.getMethod().equalsIgnoreCase("GET")) {
			keyword = URLDecoder.decode(keyword, "UTF-8");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("keyword", keyword);

		dataCount = service.dataCount(map);
		total_page = myUtil.pageCount(rows, dataCount);

		if (total_page < current_page)
			current_page = total_page;

		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;

		map.put("start", start);
		map.put("end", end);

		List<Board> list = service.listBoard(map);

		// 글번호 만들기
		int listNum, n = 0;
		Iterator<Board> it = list.iterator();
		while (it.hasNext()) {
			Board data = it.next();
			listNum = dataCount - (start + n - 1);
			data.setListNum(listNum);
			n++;
		}

        // ajax 페이징처리
        String paging = myUtil.paging(current_page, total_page);
        // String paging = myUtil.pagingMethod(current_page, total_page, "listPage");

		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("pageNo", current_page);
		model.addAttribute("paging", paging);
		model.addAttribute("total_page", total_page);
		
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		
		return "customer/board/list";
	}

	@RequestMapping(value="/customer/board/created", method=RequestMethod.GET)
	public String createdForm(
			Model model
			) throws Exception {

		model.addAttribute("pageNo", "1");
		model.addAttribute("mode", "created");
		return "customer/board/created";
	}

	@RequestMapping(value="/customer/board/created", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> createdSubmit(
			Board dto,
			HttpSession session) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		String state="true";
		
		dto.setUserId(info.getUserId());
		service.insertBoard(dto, "created");
		
		Map<String, Object> model=new HashMap<>();
		model.put("state", state);
		return model;
	}

	@RequestMapping(value="/customer/board/article")
	public String article(
			@RequestParam int boardNum,
			@RequestParam String pageNo,
			@RequestParam(defaultValue="all") String condition,
			@RequestParam(defaultValue="") String keyword,
			HttpServletRequest req,
			Model model	) throws Exception {
		if(req.getMethod().equalsIgnoreCase("GET")) { // GET 방식인 경우
			keyword = URLDecoder.decode(keyword, "utf-8");
		}
		
		service.updateHitCount(boardNum);
		
		Board dto = service.readBoard(boardNum);
		if(dto==null) {
			return "customer/error";
		}
		
        dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
         
		// 이전 글, 다음 글
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("groupNum", dto.getGroupNum());
		map.put("orderNo", dto.getOrderNo());

		Board preReadDto = service.preReadBoard(map);
		Board nextReadDto = service.nextReadBoard(map);

		model.addAttribute("dto", dto);
		model.addAttribute("preReadDto", preReadDto);
		model.addAttribute("nextReadDto", nextReadDto);
		model.addAttribute("pageNo", pageNo);
		
		return "customer/board/article";
	}

	@RequestMapping(value="/customer/board/update", method=RequestMethod.GET)
	public String updateForm(
			@RequestParam int boardNum,
			@RequestParam String pageNo,
			HttpSession session,
			Model model) throws Exception {
		
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		Board dto = service.readBoard(boardNum);
		if(dto==null) {
			return "customer/error";
		}
		
		if(! info.getUserId().equals(dto.getUserId())) {
			return "customer/error";
		}
		
		model.addAttribute("mode", "update");
		model.addAttribute("pageNo", pageNo);
		model.addAttribute("dto", dto);		

		return "customer/board/created";
	}

	@RequestMapping(value="/customer/board/update", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateSubmit(
			Board dto,
			HttpSession session) throws Exception {

		String state="true";		

		service.updateBoard(dto);

		Map<String, Object> model=new HashMap<>();
		model.put("state", state);
		return model;
	}

	@RequestMapping(value="/customer/board/reply", method=RequestMethod.GET)
	public String replyForm(
			@RequestParam int boardNum,
			@RequestParam String pageNo,
			HttpSession session,			
			Model model) throws Exception {
		
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		if(! info.getUserId().equals("admin")) {
			return "customer/error";
		}
		
		Board dto = service.readBoard(boardNum);
		if(dto==null) {
			return "customer/error";
		}
		
		String str = "["+dto.getSubject()+"] 에 대한 답변입니다.\n";
		dto.setContent(str);
		
		model.addAttribute("dto", dto);
		model.addAttribute("pageNo", pageNo);
		model.addAttribute("mode", "reply");

		return "customer/board/created";
	}

	@RequestMapping(value="/customer/board/reply", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> replySubmit(
			Board dto,
			HttpSession session) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		String state="true";
		
		dto.setUserId(info.getUserId());
		service.insertBoard(dto, "reply");
		
		Map<String, Object> model=new HashMap<>();
		model.put("state", state);
		return model;
	}
	
	@RequestMapping(value="/customer/board/delete", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(
			@RequestParam int boardNum,
			HttpSession session) throws Exception {
		
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		String state="false";
		Board dto = service.readBoard(boardNum);
		if(dto!=null) {
			if(info.getUserId().equals("admin") || info.getUserId().equals(dto.getUserId())) {
				service.deleteBoard(boardNum);
				state="true";
			}
		}
		
		Map<String, Object> model=new HashMap<>();
		model.put("state", state);
		return model;
	}
}
