#!/bin/bash

# 数据库连接信息
DB_HOST="192.168.13.247"
DB_PORT="3306"
DB_USER="root"
DB_PASS="123456"
DB_NAME="library_management"

# 执行初始化SQL脚本
echo "正在初始化数据库表结构..."
mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} < target/classes/init.sql

# 检查执行结果
if [ $? -eq 0 ]; then
    echo "✅ 数据库表结构初始化成功！"
else
    echo "❌ 数据库表结构初始化失败！"
    exit 1
fi