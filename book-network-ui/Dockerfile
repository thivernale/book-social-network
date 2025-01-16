FROM node:20.18.1-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build --prod

FROM nginx:alpine
LABEL authors="thivernale"

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/book-network-ui/browser /usr/share/nginx/html

EXPOSE 80
