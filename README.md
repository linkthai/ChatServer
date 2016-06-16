# ChatServer
chat server

-Khi user logout cần gửi một PackageLogout <br />
-Client khi nhận status OFFLINE cần phải có icon giống như HIDDEN <br />
-Client cần phải connect to server thành công trước khi hiện màn hình đăng nhập <br />
-Khi client mở lên mà chưa đăng nhập thì không tắt được <br />
-Khi client disconnect (tắt đột ngột hay bằng X) thì server sẽ tự logout và xử lý. (Logout và disconnect là khác nhau nha) <br />
-Khi client disconnect socket vẫn còn bị lỗi trong debug. (Có thể là do exception của Thread, nên sử dụng UncaughtExceptionHandler <br />
link: http://stackoverflow.com/questions/6546193/how-to-catch-an-exception-from-a-thread ) <br />
-Không cần set friend_id khi gửi PackageStatus nữa, server sẽ tự động gửi status khi client đổi status (bỏ phần set friend_id đi) <br />
-Sửa biến message của ChatMessage thành content để tránh gánh hiểu nhầm. <br />
-Đã có áp dụng message_not_seen. Sau khi gửi message thì message_not_seen của friend sẽ tăng lên 1. Bên client friend  <br />
khi nhận message nên notify người dùng, khi đã xác nhận đọc rồi thì gửi một PackageSeen đi.<br />
