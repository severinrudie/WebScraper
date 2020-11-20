import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class NaiveExtractCodeTest {

    @Test
    fun tc1() {
        val input = """
            HTTP/1.1 301 Moved Permanently
            Server: Varnish
            Retry-After: 0
            Location: https://ox.ac.uk/
            Content-Length: 0
            Accept-Ranges: bytes
            Date: Wed, 18 Nov 2020 23:26:26 GMT
            Via: 1.1 varnish
            Connection: close
            X-Served-By: cache-pao17439-PAO
            X-Cache: HIT
            X-Cache-Hits: 0
            X-Timer: S1605741987.611212,VS0,VE0
            Strict-Transport-Security: max-age=300

            )
        """.trimIndent()

        assertEquals(301, input.naiveExtractCode())
    }

    @Test
    fun tc2() {
        val input = """
            HTTP/1.1 404 Not Found
            Server: nginx
            Date: Wed, 18 Nov 2020 23:26:26 GMT
            Content-Type: text/html
            Content-Length: 162
            Connection: close

            <html>
            <head><title>404 Not Found</title></head>
            <body bgcolor="white">
            <center><h1>404 Not Found</h1></center>
            <hr><center>nginx</center>
            </body>
            </html>
            )
        """.trimIndent()

        assertEquals(404, input.naiveExtractCode())
    }

    @Test
    fun tc3() {
        val input = """
            HTTP/1.1 301 Moved Permanently
            Date: Wed, 18 Nov 2020 23:26:26 GMT
            Content-Type: text/html; charset=utf-8
            Content-Length: 85
            Connection: close
            Set-Cookie: __cfduid=d87092e6f9c8cb52dac2b49a14e29be081605741986; expires=Fri, 18-Dec-20 23:26:26 GMT; path=/; domain=.genius.com; HttpOnly; SameSite=Lax
            Location: https://genius.com/
            CF-Ray: 5f4574d858483b2e-SJC
            Cache-Control: no-cache
            Set-Cookie: no_public_cache=; path=/; max-age=0; expires=Thu, 01 Jan 1970 00:00:00 -0000
            Vary: X-Requested-With, Accept-Encoding
            Via: 1.1 vegur
            CF-Cache-Status: MISS
            cf-request-id: 067f475b3300003b2edaa42000000001
            Set-Cookie: flash=%7B%7D; path=/
            Set-Cookie: _genius_ab_test_cohort=4; Max-Age=2147483647; Path=/
            Status: 301 Moved Permanently
            X-Frame-Options: SAMEORIGIN
            X-Runtime: 9
            Server: cloudflare

            <html><body>You are being <a href="https://genius.com/">redirected</a>.</body></html>)
        """.trimIndent()

        assertEquals(301, input.naiveExtractCode())
    }

    @Test
    fun tc4() {
        val input = """
            HTTP/1.1 200 OK
            Server: nginx
            Date: Wed, 18 Nov 2020 23:26:27 GMT
            Content-Type: text/html; charset=utf-8
            Transfer-Encoding: chunked
            Connection: close
            Vary: Accept-Encoding
            Cache-Control: private
            Content-Language: en-US
            Set-Cookie: DICT_UGC=be3af0da19b5c5e6aa4e17bd8d90b28a|; domain=.youdao.com
            Set-Cookie: OUTFOX_SEARCH_USER_ID=880656908@73.223.91.12; domain=.youdao.com; expires=Fri, 11-Nov-2050 23:26:27 GMT
            Set-Cookie: JSESSIONID=abc6W5CpnTmAW9ywKRDxx; domain=youdao.com; path=/

            22d0
            <!DOCTYPE html ><html><head><meta charset="UTF-8"><meta name="keywords" content="������, ������, ������, ������, ���������, ������, ���������, ������������������, ������������, ������������, ������������, ������������, ������������, ������������, ������������"><meta name="description" content="������������������������������������������������������������������100%������������������������������������������������������������2006������������������������������������������������������������������������������������������������������������������������������������������������������������������������2014������������������������������������������������������������2018���4���������������������������������������������������������11.2������������������������������������2019���10������������������������������������������������������������DAO������������������������������������������������������"><title>������������</title><link href="https://shared.ydstatic.com/images/favicon.ico" type="image/x-icon" rel="shortcut icon"><link rel="stylesheet" href="https://shared.ydstatic.com/dict/v2016/entry/pc11.css"><script>var _rlog = _rlog || [];
                // ������ product id
                _rlog.push(["_setAccount", "dictweb"]);
                _rlog.push(["_addPost", "page", "index"]);</script></head><body><div id="nav"><a id="wljb" href="http://report.12377.cn:13225/toreportinputNormal_anis.do" target="_blank"></a><div id="more"><div><span></span> ������������</div><div class="menu"><ul><li><a href="https://cidian.youdao.com/index.html"><span class="icon_cidian"></span><div>������������������</div></a></li><li><a href="https://kiddict.youdao.com/"><span class="icon_shaoercidian"></span><div>������������������</div></a></li><li><a href="https://speak.youdao.com/"><span class="icon_kouyu"></span><div>������������</div></a></li><li><a href="https://ke.youdao.com/"><span class="icon_jingpinke"></span><div>���������������</div></a></li><li><a href="https://fanyiguan.youdao.com/?keyfrom=about.youdao"><span class="icon_fanyiguan"></span><div>���������������</div></a></li><li><a href="https://recite.youdao.com/"><span class="icon_beidanci"></span><div>���������������</div></a></li><li><a href="https://note.youdao.com/?keyfrom=about.youdao"><span class="icon_yunbiji"></span><div>���������������</div></a></li><li><a href="https://co.youdao.com/product.html?keyfrom=website"><span class="icon_xiezuo"></span><div>���������������</div></a></li><li><a href="https://zhushou.huihui.cn/"><span class="icon_huihui"></span><div>������������������</div></a></li><li><a href="https://store.youdao.com/#/general"><span class="icon_fanyiwang"></span><div>���������������</div></a></li><li><a href="http://dsp.youdao.com/dsp/website/developer.shtml?keyfrom=dict2.index"><span class="icon_zhixuan"></span><div>������������</div></a></li><li><a href="https://ai.youdao.com/"><span class="icon_zhiyun"></span><div>������������</div></a></li><li><a target="_blank" href="http://www.youdao.com/about/?keyfrom=dict2.index">������������</a></li></ul></div></div><ul><li><a target="_blank" href="http://tongdao.youdao.com/index.html" class="plan">������������ <span class="new_icon"></span></a></li><li><a href="http://www.huihui.cn/?keyfrom=dict2.index">������</a></li><li><a href="http://f.youdao.com/?keyfrom=dict2.index">������������</a></li><li><a href="http://note.youdao.com/?keyfrom=dict2.index">���������</a></li><li><a href="https://ke.youdao.com/?keyfrom=dict2.index">���������</a></li><li><a href="http://fanyi.youdao.com/?keyfrom=dict2.index">������</a></li><li><a href="https://recite.youdao.com/?vendor=chanpindaohang">���������</a></li><li><a href="https://smart.youdao.com/">������������</a></li><li><a href="http://dict.youdao.com/?keyfrom=dict2.index">������</a></li></ul></div><div id="margin1"></div><div id="logo"><div class="logo"></div></div><div id="margin2"></div><div id="search"><div class="wrap"><form method="GET" action="/search" id="form"><img class="logo" src="https://shared-https.ydstatic.com/dict/v2016/logo.png" alt="������������"><div id="selectType"><div id="type">������</div><div class="side">1</div><span class="arrow"></span><ul id="typeList"><li>������</li><li>������</li><li>������</li><li>������</li></ul></div><div id="border"><input type="text" name="le" id="translateType" value="eng"> <input type="text" name="q" onmouseover="this.focus()" onfocus="this.select()" maxlength="256" id="translateContent" autocomplete="off" placeholder="���������������������������������������"> <input type="hidden" name="keyfrom" value="dict2.index"> <span id="hnwBtn" class="hand-write"></span></div><button>������</button></form></div></div><div id="margin3"><span><a target="_blank" href="http://c.youdao.com/dict/download.html?app=dict&vendor=webdict_default&platform=win&url=http%3A%2F%2Fcodown.youdao.com%2Fcidian%2FYoudaoDict_webdict_default.exe">��������������������� </a></span>| <span><a target="_blank" href="http://cidian.youdao.com/mobile.html?keyfrom=dict2.index">��������������������� </a></span>| <span><a target="_blank" href="http://dict.youdao.com/wordbook/wordlist?keyfrom=dict2.index">��������������� </a></span>| <span class="ugc-link"><a target="_blank" href="http://pdf.youdao.com">������������ <sup>&bull;</sup></a></span></div><div id="doc2"><div class="wrap"><div class="fl"><div class="channel"><div class="popout"><div class="container"><span class="active">������������ </span><span>������������</span></div></div></div><div id="vista"></div><div id="course"><div class="category"><span class="active">��������� </span><span>������ </span><span>������������</span></div><div id="CET" class="cate start"></div><div id="IELTS" class="cate start"></div><div id="Kaoyan" class="cate start"></div><div id="GRE" class="cate start"></div><div id="Practical" class="cate start"></div></div><div id="discount" class="start"></div></div><div class="fr"><div class="popout"><iframe src="https://c.youdao.com/www/banner.html" frameborder="0"></iframe></div></div></div></div><div id="footer"><div class="wrap"><ul><li><a href="http://www.youdao.com/about/">������������</a></li><li><a href="http://ir.youdao.com/">Investors</a></li><li><a href="http://dsp.youdao.com/">������������</a></li><li><a href="http://ai.youdao.com/?keyfrom=dict2.index">������������</a></li><li><a href="http://i.youdao.com/">������������</a></li><li><a href="http://techblog.youdao.com/">������������</a></li><li><a href="http://campus.youdao.com/index.php?t1=index">������������</a></li><li><a href="http://shared.youdao.com/www/contactus-page.html">������������</a></li><li><a href="http://dict.youdao.com/map/index.html">������������</a></li><li><a href="http://www.12377.cn/">������������</a></li><li><a href="http://shared.youdao.com/images/license/businessLicense.png">������������</a></li><li><a href="https://jubao.163.com/">������������������</a></li><li><a href="http://shared.youdao.com/images/license/publicationLicense.png">������������������������</a></li><li><a href="http://shared.youdao.com/images/Broadcastingandtelevisionprogramproductionlicense.pdf">���������������������������������</a></li></ul><p class="phone">������������������������������������010-82558163 ���������������jb@rd.netease.com <span class="copy-right">&copy;<script>document.write(new Date().getFullYear());</script><a href="http://www.163.com/">������������</a> <a href="http://shared.youdao.com/dict/market/youdaoInc-v2.1/index.html#/CN">������������</a> <a href="http://xue.youdao.com/sw/m/1191866">������������</a> ���ICP���080268��� <a href="http://beian.miit.gov.cn">���ICP���10005211��� </a><a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=11010802020092" style="margin-left:5px;display:inline-block;text-decoration:none;vertical-align:-9%"><img src="https://shared.ydstatic.com/images/icons/jgw.jpg" style="float:left;width:14px"> <span style="float:left;line-height:17px">��������������� 11010802020092���</span></a></span></p></div></div><a id="backToTop" href="javascript:void(0);">���������������</a><div id="handWrite" class="pm" style="display:none"><object width="346" height="216" type="application/x-shockwave-flash" id="hnw" data="https://shared.ydstatic.com/dict/v5.16/swf/Hnw.swf"><param name="movie" value="            $\{DictStaticBase}            swf/Hnw.swf"><param name="menu" value="false"><param name="allowScriptAccess" value="always"></object></div><div id="callback"></div><script src="https://shared.ydstatic.com/js/jquery/jquery-1.12.3.min.js"></script><script src="https://shared.ydstatic.com/dict/v2016/entry/jquery.mousewheel.js"></script><script src="https://shared.ydstatic.com/dict/v2016/entry/jquery.placeholder.js"></script><script src="https://shared.ydstatic.com/dict/v2016/entry/autocomplete_json.js"></script><script src="https://shared.ydstatic.com/dict/v2016/entry/pc5.js"></script><script defer="defer" src="https://shared.ydstatic.com/js/rlog/v1.js"></script></body></html>
            0

            )
        """.trimIndent()

        assertEquals(200, input.naiveExtractCode())
    }
}
