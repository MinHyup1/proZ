package com.kh.spring.memo.model.service;

import java.util.List;
import java.util.Map;

import com.kh.spring.member.model.dto.Member;
import com.kh.spring.memo.model.dto.Memo;
import com.kh.spring.project.model.dto.ProjectMember;

public interface MemoService {

	Memo insertMemo(Memo memo, Member member);

	List<Memo> selectMemoByWsIdx(String wsIdx);
	
	List<Memo> selectMemoByWsIdxAsc(String wsIdx);

	ProjectMember selectProjectMember(String userIdx, String wsIdx);

	void deleteMemoByMemoIdx(String memoIdx);

	void updateMemoByMemoIdx(Memo memo);

	List<Map<String, Object>> selectMemoBySearch(String wsIdx,String keyword);

	

	List<Map<String, Object>> selectMemoAndWriterByWsIdxAsc(String wsIdx);

	List<Map<String, Object>> selectMemoAndWriterByWsIdxDesc(String wsIdx);

	List<Map<String, Object>> selectMemoByTop(String projectIdx); 
	
	
}
