#!/bin/bash
IMAGE_NAME="wjdjoonim/core-bank-fisa"
IMAGE_TAG=${1:-latest}

echo "🔄 최신 Core Bank 이미지 Pull..."
docker pull "${IMAGE_NAME}:${IMAGE_TAG}"

echo "🧹 기존 컨테이너 종료..."
export IMAGE_TAG
docker compose down

echo "🚀 새로운 버전으로 실행..."
docker compose up -d

echo "✅ 배포 완료!"