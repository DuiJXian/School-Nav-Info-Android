import 'package:flutter/material.dart';
import 'package:html/parser.dart';
import 'package:http/http.dart' as http;

class ScoreViewModel extends ChangeNotifier {
  final String _cookie;

  late String _selectScoreTerm = getTermList().first;

  String get selectScoreTerm => _selectScoreTerm;

  ScoreViewModel({required String cookie}) : _cookie = cookie;

  //选择成绩课表学期
  void setScoreTerm(String val) {
    _selectScoreTerm = val;
    notifyListeners();
  }

  Future<List<List<String>>> getScoreTable() async {
    final tableUrl = Uri.parse(
      'http://zswxyjw.yinghuaonline.com/znlykjdxswxy_jsxsd/kscj/cjcx_list',
    );
    final tableResp = await http.post(
      tableUrl,
      headers: {'Cookie': _cookie},
      body: {'kksj': _selectScoreTerm, 'kcxz': '', 'kcmc': '', 'xsfs': 'all'},
    );
    return extractTableData(tableResp.body);
  }

  Future<List<List<String>>> extractTableData(String html) async {
    // logger.e(html);
    var document = parse(html);
    final trs = document.querySelector('#dataList')!.querySelectorAll('tr');
    List<List<String>> tableData = [];
    for (var i = 1; i < trs.length; i++) {
      final tr = trs[i];
      final cells = tr.querySelectorAll("td");

      final lineText = cells.map((e) => e.text).toList();

      tableData.add([lineText[3], lineText[4], lineText[6], lineText[10]]);
    }
    return tableData;
  }

  List<String> getTermList() {
    final now = DateTime.now();
    int year = now.year;
    int month = now.month;
    late String startYear, endYear;
    int term;
    if (month >= 8) {
      // 上学期：8~1月
      startYear = year.toString();
      endYear = (year + 1).toString();
      term = 1;
    } else {
      // 下学期：2~7月
      startYear = (year - 1).toString();
      endYear = year.toString();
      term = 2;
    }

    String startTerm = "$startYear-$endYear-$term";
    List<String> termSplit = startTerm.split('-');
    int firstYear = int.parse(termSplit[0]);
    int lastYear = int.parse(termSplit[1]);
    int upOrDown = int.parse(termSplit[2]);
    List<String> termList = [startTerm];

    for (int i = 1; i < 8; i++) {
      if (upOrDown == 2) {
        upOrDown--;
      } else {
        firstYear--;
        lastYear--;
        upOrDown++;
      }
      termList.add("$firstYear-$lastYear-$upOrDown");
    }

    return termList;
  }
}
