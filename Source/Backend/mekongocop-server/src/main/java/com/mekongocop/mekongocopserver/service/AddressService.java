package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.dto.AddressDTO;
import com.mekongocop.mekongocopserver.dto.UserDTO;
import com.mekongocop.mekongocopserver.entity.Address;
import com.mekongocop.mekongocopserver.entity.User;
import com.mekongocop.mekongocopserver.repository.AddressRepository;
import com.mekongocop.mekongocopserver.repository.UserRepository;
import com.mekongocop.mekongocopserver.util.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    public Address convertAddressDTOToAddress(AddressDTO addressDTO) {
        Address address = new Address();

        // Gán địa chỉ ID và mô tả địa chỉ
        address.setAddress_id(addressDTO.getAddressId());
        address.setAddress_description(addressDTO.getAddressDescription());

        User user = new User();
        user.setUser_id(addressDTO.getUserId());
        address.setUser(user);

        return address;
    }



    public AddressDTO convertAddressToAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();

        // Gán giá trị từ Address sang AddressDTO
        addressDTO.setAddressId(address.getAddress_id());
        addressDTO.setAddressDescription(address.getAddress_description());

        // Lấy userId từ User trong Address
        if (address.getUser() != null) {
            addressDTO.setUserId(address.getUser().getUser_id());
        }

        return addressDTO;
    }

    public AddressDTO addAddress(AddressDTO addressDTO, String token) {
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }
            addressDTO.setUserId(userId);
            addressDTO.setAddressDescription(addressDTO.getAddressDescription());
            Address address = convertAddressDTOToAddress(addressDTO);
            addressRepository.save(address);
            return convertAddressToAddressDTO(address);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public AddressDTO updateAddress(int addressId, AddressDTO addressDTO, String token) {
        try {
            // Lấy userId từ token
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }

            // Lấy địa chỉ cần cập nhật
            Optional<Address> existingAddressOptional = addressRepository.findById(addressId);
            if (!existingAddressOptional.isPresent()) {
                throw new IllegalArgumentException("Address not found");
            }

            Address existingAddress = existingAddressOptional.get();

            // Kiểm tra xem địa chỉ có thuộc về người dùng này không
            if (existingAddress.getUser().getUser_id() != userId) {
                throw new IllegalArgumentException("You are not allowed to update this address");
            }

            // Cập nhật thông tin địa chỉ
            existingAddress.setAddress_description(addressDTO.getAddressDescription());

            // Lưu thay đổi
            addressRepository.save(existingAddress);

            // Trả về DTO của địa chỉ đã cập nhật
            return convertAddressToAddressDTO(existingAddress);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred.", e);
        }
    }


    public void deleteAddress(String token, int addressId) {
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }
            addressRepository.deleteById(addressId);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<AddressDTO> getAllAddresses(String token) {
        try{
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found");
            }
            List<Address> addressList = addressRepository.findAllByUserId(userId);
            List<AddressDTO> addressDTOList = addressList.stream().map(this::convertAddressToAddressDTO).collect(Collectors.toList());
            return addressDTOList;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }



}
