#!/bin/bash

echo "🔄 최신 Core Bank 이미지 Pull..."
docker pull wjdjoonim/core-bank-fisa:latest

echo "🧹 기존 컨테이너 종료..."
docker compose down

echo "🚀 새로운 버전으로 실행..."
docker compose up -d

echo "✅ 배포 완료!"