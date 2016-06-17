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
---------------------------------------------------------------------------------------------
-Thêm group conversation
-Khi đăng nhập nên gửi một PackageGroupConversation với action là CONVERSATION để lấy một ArrayList<GroupConversation> <br />
-Các action khác của PackageGroupConversation thì nên đọc kĩ trong hàm beginGroupConversation của ClientConnetion <br />
-Mọi action (trừ CREATE và CONVERSATION thì chỉ gửi lại gói về người gửi) đều sẽ gửi cho các thành viên trong group tính cả người gửi <br />
-Khi gửi PackageMessage có thêm một biến groupConversation, mặc định set là false, khi gửi cho group thì nên set true <br />
-Gói seen cũng có biến groupConversation tương tự nhưng k có mặc định, khi gửi phải set cụ thể true hoặc false <br />
