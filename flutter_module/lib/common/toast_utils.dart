
import 'package:flutter_module/them/app_colors.dart';
import 'package:fluttertoast/fluttertoast.dart';

class ToastUtils{

  static void showToast(String msg){
    Fluttertoast.showToast(
        msg: msg,
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.TOP,
        backgroundColor: AppColors.light.primary,
        textColor: AppColors.light.bgPrimary,
        fontSize: 16
        );
  }

}