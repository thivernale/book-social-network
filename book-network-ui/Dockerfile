FROM node:20.18.1-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build --prod

FROM nginx:alpine
LABEL authors="thivernale"
LABEL org.opencontainers.image.source="https://github.com/thivernale/book-social-network"

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/book-network-ui/browser /usr/share/nginx/html

EXPOSE 80

# Run locally with:
#  docker build -t bsn/bsn-ui:1.0.0 -f ./book-network-ui/Dockerfile ./book-network-ui/
