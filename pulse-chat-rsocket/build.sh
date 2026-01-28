#!/bin/bash

# 設定你的容器名稱
VERSION=$(date +%Y%m%d-%H%M)
IMAGE_NAME="push-chat"

echo "📦 Maven 打包中..."
if mvn clean package -DskipTests; then
    echo "✅ 打包成功"
else
    echo "❌ 打包失敗，請檢查代碼"
    exit 1
fi

echo "構建 Docker 鏡像..."
docker build -t $IMAGE_NAME:$VERSION .
docker build -t $IMAGE_NAME:latest .

echo "Build 完成！版本號: $VERSION"
echo "🧹 清理舊的懸空映像檔..."
docker image prune -f