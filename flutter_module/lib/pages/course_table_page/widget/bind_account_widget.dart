import 'package:flutter/material.dart';
import 'package:flutter_module/pages/course_table_page/view_models/course_table_view_model.dart';
import 'package:provider/provider.dart';

class BindAccountWidget extends StatelessWidget {
  const BindAccountWidget({super.key});

  void onBind(CourseTableViewModel viewModel) {
    viewModel.login(
      viewModel.usernameController.text,
      viewModel.passwordController.text,
    );
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = context.read<CourseTableViewModel>();
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              "初次使用请先绑定教务系统",
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 10),
            SizedBox(
              width: 300,
              child: TextField(
                controller: viewModel.usernameController,
                decoration: InputDecoration(
                  labelText: "用户名",
                  border: OutlineInputBorder(),
                ),
              ),
            ),
            SizedBox(height: 16),
            SizedBox(
              width: 300,
              child: TextField(
                obscureText: true,
                controller: viewModel.passwordController,
                decoration: InputDecoration(
                  labelText: "密码",
                  border: OutlineInputBorder(),
                ),
              ),
            ),
            SizedBox(height: 10),
            if (viewModel.loginFlag == 1)
              Text("用户名或密码错误", style: TextStyle(color: Colors.red[400])),
            ElevatedButton(
              onPressed: () {
                onBind(viewModel);
              },
              child: Text("绑定"),
            ),
          ],
        ),
      ),
    );
  }
}
