import http from 'k6/http';
import { check, sleep } from 'k6';

// Cấu hình tải
export const options = {
    stages: [
        { duration: '2m', target: 10000 }, // Tăng dần đến 10.000 người dùng trong 2 phút
        { duration: '10m', target: 10000 }, // Duy trì 10.000 người dùng trong 10 phút
        { duration: '2m', target: 0 },     // Giảm dần về 0 người dùng
    ],
};

export default function () {
    // Gửi một yêu cầu POST để mô phỏng việc gửi thông báo
    const url = 'http://localhost:8080/api/v1/admin/notification/send?message=WelcomeToMekongOCOP';

    const params = {
        headers: {
            'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyLHRpdGFuc2RldiIsImlzcyI6IlRpdGFuc0RldiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzMxNjYxODgzLCJleHAiOjE3MzE2NjU0ODN9.EmHW8mCKBFn9wWJ58SphtWNsOpzv_2QrGAuoxdIqqi3rJGQsc-UMSqazP90F-gY04b3Dj9pYBo8L1T-D5pJcVQ',
        },
    };

    const res = http.post(url, null, params);

    // Kiểm tra phản hồi
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    // Nghỉ giữa các lần yêu cầu
    sleep(1);
}
