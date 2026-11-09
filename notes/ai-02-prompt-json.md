# AI Lab 第02课：把回答收成 JSON（prompt-json）

## 学了啥
让模型只交一个 JSON：`{"intent":"...","question":"原话"}`。`intent` 必须在允许列表（check_in/out、breakfast、parking、pets、wifi、cancel、unknown）。提取先去围栏再括号配对，然后 `json.loads`（禁 eval）。字段不合法=解析失败；失败就把原文放回 `assistant` 再要一次，最多两次。Mock 下「入住」「宠物」「地铁」分别得 check_in/pets/unknown（均第1次）；人话→JSON 修复用了2次；第一句已合法则1次；「入住日前取消」因关键词顺序归 cancel。

## 为什么能跑
`classify_intent` 看 system 里的【意图分类】标记，Mock 走 `_intent_response` 按关键词吐 JSON。`extract_json_object` + `parse_intent_payload` 校验形状与允许列表；`swim` 等列表外值当失败再修。练习不联网、不读密钥。

## 卡过啥
- 别用 `eval`；别解析失败就猜意图。
- 修复必须带回失败原文；允许列表要在代码里再查。
- Mock「取消优先入住」是脚本规则，不是 NLU；一律 `.venv` 的 python。
