#!/bin/bash

# 디렉토리로 이동
cd /Users/zzambab98/Documents/senior-care-watch

# 토큰이 포함된 이전 스크립트 파일 제거
rm -f git_update_all.sh setup_new_repo.sh git_force_push.sh git_merge_push.sh git_commit_push.sh git_commit_push_fixed.sh

# 기존 Git 설정 제거 (새로운 리포지토리이므로)
rm -rf .git

# 새 Git 리포지토리 초기화
git init

# 모든 파일 추가
git add .

# 초기 커밋
git commit -m "Initial commit with complete project code"

# 원격 저장소 추가
git remote add origin https://github.com/asula1/senior-care-admin.git

echo "설정 완료! 이제 GitHub에 로그인한 상태에서 다음 명령어를 실행하세요:"
echo "git push -u origin main"
