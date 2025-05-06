import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_module/common/toast_utils.dart';
import 'package:flutter_module/main.dart';
import 'package:flutter_module/them/app_colors.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:html/parser.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class CourseTableViewModel extends ChangeNotifier {
  final usernameController = TextEditingController(text: "2023021737");
  final passwordController = TextEditingController(text: "XiaoZhuo2002.");

  bool _bindFlag = false;
  int _loginFlag = 0;
  bool _dateSelectFlag = false;
  String _username = "";
  String _password = "";
  String _cookie = "";
  int _weekNumber = 0;
  late String _selectTableTerm;


  int get weekNumber => _weekNumber;
  bool get bindFlag => _bindFlag;
  int get loginFlag => _loginFlag;
  bool get dateSelectFlag => _dateSelectFlag;
  String get selectTerm => _selectTableTerm;
  String get cookie => _cookie;

  CourseTableViewModel() {
    _selectTableTerm = getTermList()[0];
    _loadDate();
  }

  //定义颜色列表（最多12个）
  final List<Color> colorList = [
    Colors.red.shade100,
    Colors.green.shade100,
    Colors.blue.shade100,
    Colors.orange.shade100,
    Colors.purple.shade100,
    Colors.cyan.shade100,
    Colors.yellow.shade100,
    Colors.pink.shade100,
    Colors.teal.shade100,
    Colors.brown.shade100,
    Colors.indigo.shade100,
    Colors.lime.shade100,
  ];

  //获取某个课程对应的颜色
  Color getColorForCell(String cellValue, List<String> uniqueList) {
    if (cellValue.trim().isEmpty) return Colors.transparent;
    final index = uniqueList.indexOf(cellValue);
    return colorList[index % colorList.length]; // 避免越界
  }

  //判断某一列是否为空
  bool isColumnEmpty(List<List<String>> grid, int columnIndex) {
    return grid.every((row) => row[columnIndex].isEmpty);
  }

  //选择成绩课表学期
  void setSelectTableTerm(String val) {
    _selectTableTerm = val;
    notifyListeners();
  }

  //下一周
  void increaseWeekNumber() {
    if (weekNumber < 20 && weekNumber != 0) {
      _weekNumber++;
      notifyListeners();
    }else{
      ToastUtils.showToast("请先选择课程开始日期");
    }
  }

  //上一周
  void decreaseWeekNumber() {
    if (weekNumber > 1 && weekNumber != 0) {
      _weekNumber--;
      notifyListeners();
    }else{
      ToastUtils.showToast("请先选择课程开始日期");
    }
  }

  //保存选取周次的日期到本地
  void setWeekStartDate(String selectedDate) async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString("weekStartDate", selectedDate);
    _weekNumber = _getWeekNumberFromDate(selectedDate);
    notifyListeners();
  }

  //加载本地数据
  void _loadDate() async {
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    _username = prefs.getString("username") ?? "";
    _password = prefs.getString("password") ?? "";
    String weekStartDate = prefs.getString("weekStartDate") ?? "";
    if (_username.isNotEmpty && _password.isNotEmpty) {
      //绑定判断
      _bindFlag = true;
      logger.d("login");
      _cookie = await login(_username, _password);
      logger.d("_loadDate$_cookie");
      if (_cookie.isEmpty) {
        //登陆失败
        prefs.setString("username", "");
        prefs.setString("password", "");
        _loginFlag = 1;
      } else {
        _loginFlag = 2;
      }
    }

    if (weekStartDate.isNotEmpty) {
      _dateSelectFlag = true;
      _weekNumber = _getWeekNumberFromDate(weekStartDate);
    }
    notifyListeners();
  }

  //根据起始日期计算周数
  int _getWeekNumberFromDate(String date) {
    final difference = DateTime.now().difference(
      DateTime.parse(date),
    );
    final days = difference.inDays;
    return (days / 7).ceil();
  }

  //将二维课表变成一维，用于计算颜色
  List<String> getUniqueNonEmpty(List<List<String>> data) {
    final Set<String> set = {};
    for (var row in data) {
      for (var cell in row) {
        if (cell.trim().isNotEmpty) {
          set.add(cell);
        }
      }
    }
    return set.toList();
  }

  //登录
  Future<String> login(String username, String password) async {
    final loginUrl = Uri.parse(
      "http://zswxyjw.yinghuaonline.com/znlykjdxswxy_jsxsd/xk/LoginToXk",
    );

    String encoded = "${encodeInp(username)}%%%${encodeInp(password)}";

    final loginResp = await http.post(
      loginUrl,
      headers: {"Content-Type": "application/x-www-form-urlencoded"},
      body: {"encoded": encoded},
    );
    //登陆成功会重定向302，200表示未成功
    final setCookie = loginResp.headers["set-cookie"];
    _cookie = setCookie != null ? setCookie.split(';').first : "";
    if (loginResp.statusCode == 302) {
      _loginFlag = 2;
      _bindFlag = true;
      _username = username;
      _password = password;
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      prefs.setString("username", username);
      prefs.setString("password", password);
    } else {
      _loginFlag = 1;
      _bindFlag = false;
      passwordController.text = "用户名或密码错误";
    }
    notifyListeners();
    return cookie;
  }

  //将爬取的课程字符格式化
  String textFormat(String text) {
    if (text.isEmpty) return "";
    var textList = text.split(',');
    return "${textList[0]}-${textList[1]}\n${textList[2]}";
  }

  //获取课表信息
  Future<List<List<String>>> getTimeTable() async {
    logger.d("getTimeTable:$_cookie");
    final tableUrl = Uri.parse(
      'http://zswxyjw.yinghuaonline.com/znlykjdxswxy_jsxsd/xskb/xskb_list.do',
    );
    final tableResp = await http.post(
      tableUrl,
      headers: {'Cookie': _cookie},
      body: {
        'cj0701id': '',
        'zc': weekNumber == 0 ? '' : '$weekNumber',
        'demo': '',
        'xnxq01id': _selectTableTerm,
        'sfFD': '1',
      },
    );
    return extractTableData(tableResp.body);
  }

  //base64编码
  String encodeInp(String input) {
    List<int> bytes = utf8.encode(input);
    return base64.encode(bytes);
  }

  //提取课表数据
  List<List<String>> extractTableData(String html) {
    var document = parse(html);
    final List<List<String>> result = [];
    final rows = document.querySelector('#kbtable')!.querySelectorAll('tr');
    for (var i = 1; i < rows.length - 1; i++) {
      final row = rows[i];
      final cells = row.querySelectorAll('td');
      final List<String> rowCourses = [];

      for (var j = 0; j < cells.length; j++) {
        final div = cells[j].querySelector('.kbcontent');
        final lines =
            div!.innerHtml
                .split('<br>')
                .map((e) => parse(e).body?.text.trim() ?? '')
                .toList();
        if (lines.length > 1) {
          var courseName = lines[0].replaceAll(RegExp(r'\[.*?\]| '), '');
          var courseTeacher = lines[1].replaceAll(' ', '');
          var courseLocation = lines[4].replaceAll(' ', '');
          var res = '$courseName,$courseTeacher,$courseLocation';
          rowCourses.add(res);
        } else {
          rowCourses.add('');
        }
      }
      result.add(rowCourses);
    }
    return result;
  }

  //获取学期列表
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
