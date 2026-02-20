package com.firsttech.assistant.plugin
object BuiltinPlugins {
    fun getAll(): List<Plugin> = listOf(
        Plugin(name="일정조회",description="일정 조회",keywords="일정,스케줄,약속,미팅",scriptType="builtin",scriptCode="[]",category="관리"),
        Plugin(name="지출분석",description="지출 분석",keywords="지출,소비,카드,결제",scriptType="builtin",scriptCode="[]",category="분석"),
        Plugin(name="고정지출관리",description="고정지출 관리",keywords="고정지출,반복결제,구독",scriptType="builtin",scriptCode="[]",category="관리"),
        Plugin(name="통화기록검색",description="통화기록 조회",keywords="통화,전화,연락,수신,발신",scriptType="builtin",scriptCode="[]",category="통신"),
        Plugin(name="문자검색",description="SMS 검색",keywords="문자,메시지,SMS,택배",scriptType="builtin",scriptCode="[]",category="검색"),
        Plugin(name="연락처검색",description="연락처 검색",keywords="연락처,전화번호,주소록",scriptType="builtin",scriptCode="[]",category="검색"),
        Plugin(name="사진지역분류",description="사진 지역별 분류",keywords="사진,지역,분류,GPS",scriptType="builtin",scriptCode="[]",category="미디어"),
        Plugin(name="택배확인",description="택배 배송 확인",keywords="택배,배송,배달,운송장",scriptType="builtin",scriptCode="[]",category="검색"),
        Plugin(name="입출금확인",description="입출금 확인",keywords="입금,출금,이체,송금,은행",scriptType="builtin",scriptCode="[]",category="분석"),
        Plugin(name="통화통계",description="통화 통계",keywords="통화통계,자주,시간",scriptType="builtin",scriptCode="[]",category="분석"),
    )
}
