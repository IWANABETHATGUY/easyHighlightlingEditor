# easyHighlightlingEditor
## 一个简易的支持高亮的编辑器
> 使用dfa做的lexer获得文本的分类，使用jPanetext 中的styleDocument，对分类的token进行着色，这是第一个版本，做的有些粗糙，每次都是全文着色，在文本量大了以后，可能会有卡顿。

![easyHighLightingEditor](http://ouck2t8ui.bkt.clouddn.com/easyHighLightingEditor_project.png)
>editor主要是gui的着色，以及事件绑定  
>TextUtil.java 主要是一些常用的函数  
>Token.java 是一个token实体  
>Dfa.java c- 语言的词法分析器

![easyHightingExample](http://ouck2t8ui.bkt.clouddn.com/highLightingExample.gif)
- [x] @mentions, #refs, [links](), **formatting**, and  supported
- [x] list syntax required (any unordered or ordered list supported)
- [x] this is a complete item
- [ ] this is an incomplete item