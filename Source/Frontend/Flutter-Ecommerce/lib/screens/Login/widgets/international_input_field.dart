import 'package:flutter/material.dart';
import 'package:intl_phone_number_input/intl_phone_number_input.dart';

class PhoneInput extends StatefulWidget {
  const PhoneInput({Key? key}) : super(key: key);

  @override
  State<PhoneInput> createState() => _PhoneInputState();
}

class _PhoneInputState extends State<PhoneInput> {
  TextEditingController controller = TextEditingController();

  PhoneNumber number = PhoneNumber(isoCode: 'NG');

  @override
  Widget build(BuildContext context) {
    return InternationalPhoneNumberInput(
      onInputChanged: (number) {
        // getPhoneNumber(number.phoneNumber.toString());
        // print(this.number);
      },
      // onInputValidated: (bool value) {
      //   print(value);
      // },
      maxLength: 10,
      spaceBetweenSelectorAndTextField: 0.0,
      selectorConfig: const SelectorConfig(
        selectorType: PhoneInputSelectorType.BOTTOM_SHEET,
        leadingPadding: 10.0,
        showFlags: true,
        setSelectorButtonAsPrefixIcon: true,
      ),
      ignoreBlank: false,
      autoValidateMode: AutovalidateMode.disabled,
      selectorTextStyle: const TextStyle(color: Colors.black),
      initialValue: number,
      textFieldController: controller,
      formatInput: false,
      keyboardType:
          const TextInputType.numberWithOptions(signed: true, decimal: true),
      inputDecoration: const InputDecoration(
        border: InputBorder.none,
        hintText: '307-312-0835',
      ),
      onSaved: (PhoneNumber number) {
        // getPhoneNumber(number.phoneNumber.toString());
      },
    );
  }

  void getPhoneNumber(String phoneNumber) async {
    PhoneNumber number =
        await PhoneNumber.getRegionInfoFromPhoneNumber(phoneNumber, ' ');

    setState(() {
      this.number = number;
    });
  }
}
