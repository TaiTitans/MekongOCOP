import 'package:flutter/material.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Utils/Constants/app_constants.dart';
import 'package:smart_shop/Utils/font_styles.dart';

class PrivacyPolicy extends StatelessWidget {
  static const String routeName = 'privacypolicy';
  const PrivacyPolicy({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(context),
      body: _buildBody(context),
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize:
      Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Chính sách bảo mật',
        fixedHeight: 100.0,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(20.0),
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Chính sách bảo mật',
              style: FontStyles.montserratBold17().copyWith(
                  fontSize: 18.0, decoration: TextDecoration.underline),
            ),
            const SizedBox(height: 10.0),
            Text(
              'Mekong OCOP cam kết bảo vệ quyền riêng tư và dữ liệu cá nhân của bạn khi sử dụng sàn thương mại điện tử của chúng tôi. Chính sách này giải thích cách chúng tôi thu thập, sử dụng, lưu trữ và bảo vệ thông tin cá nhân của bạn. Bằng cách sử dụng ứng dụng Mekong OCOP, bạn đồng ý với các điều khoản trong chính sách này.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '1. Thông tin chúng tôi thu thập',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Chúng tôi thu thập các thông tin sau:\n'
                  '- Thông tin cá nhân: Khi bạn đăng ký tài khoản hoặc mua sắm trên sàn Mekong OCOP, chúng tôi có thể yêu cầu bạn cung cấp tên, địa chỉ email, số điện thoại, địa chỉ giao hàng, và các thông tin cần thiết khác.\n'
                  '- Thông tin về giao dịch: Chúng tôi thu thập thông tin liên quan đến các giao dịch của bạn trên ứng dụng, bao gồm các sản phẩm đã mua, phương thức thanh toán, và địa chỉ giao hàng.\n'
                  '- Thông tin tự động thu thập: Khi bạn sử dụng ứng dụng, chúng tôi có thể thu thập tự động một số thông tin như địa chỉ IP, loại thiết bị, phiên bản hệ điều hành, và dữ liệu sử dụng ứng dụng để tối ưu hóa trải nghiệm người dùng.\n'
                  '- Cookies và công nghệ theo dõi: Chúng tôi có thể sử dụng cookies để lưu trữ các thông tin liên quan đến hoạt động của bạn trên ứng dụng nhằm cá nhân hóa trải nghiệm và phân tích hoạt động.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '2. Mục đích sử dụng thông tin',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Chúng tôi sử dụng thông tin của bạn cho các mục đích sau:\n'
                  '- Cung cấp và duy trì dịch vụ: Thông tin của bạn giúp chúng tôi quản lý tài khoản, xử lý đơn hàng, cung cấp dịch vụ chăm sóc khách hàng, và hỗ trợ kỹ thuật.\n'
                  '- Cải thiện dịch vụ: Chúng tôi phân tích thông tin để hiểu rõ hơn về nhu cầu của người dùng, từ đó cải tiến trải nghiệm và chức năng của sàn Mekong OCOP.\n'
                  '- Tiếp thị: Chúng tôi có thể sử dụng thông tin liên lạc của bạn để gửi các chương trình khuyến mãi, thông tin sản phẩm mới, hoặc các dịch vụ liên quan mà bạn có thể quan tâm (có tùy chọn từ chối nhận các thông báo này).\n'
                  '- Tuân thủ pháp luật: Chúng tôi có thể sử dụng và chia sẻ thông tin để đáp ứng các yêu cầu pháp lý hoặc bảo vệ quyền lợi hợp pháp của chúng tôi, người dùng, và cộng đồng.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '3. Chia sẻ thông tin cá nhân',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Chúng tôi chỉ chia sẻ thông tin cá nhân của bạn trong các trường hợp sau:\n'
                  '- Với các bên thứ ba đáng tin cậy: Chúng tôi có thể chia sẻ thông tin với các đối tác cung cấp dịch vụ thanh toán, vận chuyển, và quản lý đơn hàng nhằm hoàn tất các giao dịch của bạn.\n'
                  '- Yêu cầu pháp lý: Chúng tôi có thể chia sẻ thông tin của bạn để tuân thủ các quy định pháp luật, yêu cầu của tòa án hoặc các cơ quan chính phủ.\n'
                  '- Chuyển giao kinh doanh: Nếu sàn Mekong OCOP bị sáp nhập, mua lại, hoặc bán, thông tin cá nhân của bạn có thể được chuyển giao cho bên thứ ba như một phần của giao dịch kinh doanh.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '4. Bảo mật thông tin',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Chúng tôi áp dụng các biện pháp kỹ thuật và tổ chức hợp lý để bảo vệ thông tin cá nhân của bạn khỏi truy cập trái phép, mất mát, hoặc rò rỉ. Tuy nhiên, không có hệ thống nào có thể đảm bảo an toàn tuyệt đối, và chúng tôi không chịu trách nhiệm trong trường hợp rủi ro không thể tránh khỏi.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '5. Quyền của người dùng',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Bạn có quyền kiểm soát thông tin cá nhân của mình, bao gồm:\n'
                  '- Truy cập và chỉnh sửa: Bạn có thể truy cập và chỉnh sửa thông tin cá nhân trong tài khoản của mình bất cứ lúc nào thông qua ứng dụng.\n'
                  '- Xóa thông tin: Bạn có quyền yêu cầu chúng tôi xóa thông tin cá nhân của bạn khi không còn cần thiết cho mục đích đã thu thập, trừ khi chúng tôi cần giữ lại thông tin để tuân thủ pháp luật hoặc các yêu cầu khác.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '6. Thời gian lưu trữ thông tin',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Chúng tôi sẽ lưu trữ thông tin cá nhân của bạn chỉ trong thời gian cần thiết để thực hiện các mục đích đã nêu trong chính sách này, hoặc theo yêu cầu pháp luật.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '7. Chính sách đối với trẻ em',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Mekong OCOP không nhắm đến và không cố ý thu thập thông tin cá nhân từ trẻ em dưới 13 tuổi. Nếu chúng tôi phát hiện rằng đã thu thập thông tin từ trẻ em dưới độ tuổi này mà không có sự đồng ý của cha mẹ hoặc người giám hộ, chúng tôi sẽ xóa thông tin đó.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              '8. Cập nhật chính sách bảo mật',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Chúng tôi có thể cập nhật chính sách bảo mật này theo thời gian. Nếu có bất kỳ thay đổi nào quan trọng, chúng tôi sẽ thông báo cho bạn qua email hoặc thông báo trong ứng dụng. Bạn nên kiểm tra thường xuyên để cập nhật thông tin mới nhất.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
            const SizedBox(height: 10.0),
            Text(
              'Liên hệ với chúng tôi',
              style: FontStyles.montserratBold14(),
            ),
            const SizedBox(height: 5.0),
            Text(
              'Nếu bạn có bất kỳ câu hỏi nào về chính sách bảo mật này hoặc cách chúng tôi xử lý thông tin cá nhân của bạn, vui lòng liên hệ với chúng tôi qua thông tin liên hệ trên ứng dụng.',
              style: FontStyles.montserratRegular14()
                  .copyWith(fontSize: 15.0, wordSpacing: 1.5, height: 1.5),
            ),
          ],
        ),
      ),
    );
  }
}
