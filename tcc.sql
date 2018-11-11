-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: 02-Out-2018 às 21:39
-- Versão do servidor: 5.7.19
-- PHP Version: 7.1.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tcc`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `mensagem`
--

DROP TABLE IF EXISTS `mensagem`;
CREATE TABLE IF NOT EXISTS `mensagem` (
  `idMensagem` int(11) NOT NULL AUTO_INCREMENT,
  `mensagem` varchar(500) NOT NULL,
  `idProjeto` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idMensagem`),
  KEY `idProjeto` (`idProjeto`),
  KEY `idUsuario` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `projeto`
--

DROP TABLE IF EXISTS `projeto`;
CREATE TABLE IF NOT EXISTS `projeto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome_projeto` varchar(500) NOT NULL,
  `imagem` longblob,
  `ativo` tinyint(1) NOT NULL,
  `repositorio_git` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `relacao_projeto_usuario`
--

DROP TABLE IF EXISTS `relacao_projeto_usuario`;
CREATE TABLE IF NOT EXISTS `relacao_projeto_usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idUsuario` int(11) NOT NULL,
  `idProjeto` int(11) NOT NULL,
  `msg_nao_lida` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_usuario` (`idUsuario`),
  KEY `fk_projeto` (`idProjeto`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(500) NOT NULL,
  `email` varchar(500) NOT NULL,
  `nome` varchar(500) NOT NULL,
  `imagem` longblob NOT NULL,
  `senha` varchar(500) NOT NULL,
  `pergunta1` varchar(50) NOT NULL,
  `resposta1` varchar(50) NOT NULL,
  `pergunta2` varchar(50) NOT NULL,
  `resposta2` varchar(50) NOT NULL,
  `primeiro_login` tinyint(1) NOT NULL DEFAULT '1',
  `nivel_de_permissao` int(11) NOT NULL,
  `ativo` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `usuarios`
--

INSERT INTO `usuarios` (`id`, `usuario`, `email`, `nome`, `imagem`, `senha`, `pergunta1`, `resposta1`, `pergunta2`, `resposta2`, `primeiro_login`, `nivel_de_permissao`, `ativo`) VALUES
(1, 'admin', '', '', '', '21232f297a57a5a743894a0e4a801fc3', '', '', '', '', 1, 3, 1);

-- --------------------------------------------------------

--
-- Estrutura da tabela `website`
--

DROP TABLE IF EXISTS `website`;
CREATE TABLE IF NOT EXISTS `website` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `link` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `website`
--

INSERT INTO `website` (`id`, `link`) VALUES
(1, 'http://localhost:52709/DevHubWeb/');

--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `mensagem`
--
ALTER TABLE `mensagem`
  ADD CONSTRAINT `mensagem_ibfk_1` FOREIGN KEY (`idProjeto`) REFERENCES `projeto` (`id`),
  ADD CONSTRAINT `mensagem_ibfk_2` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`id`);

--
-- Limitadores para a tabela `relacao_projeto_usuario`
--
ALTER TABLE `relacao_projeto_usuario`
  ADD CONSTRAINT `fk_projeto` FOREIGN KEY (`idProjeto`) REFERENCES `projeto` (`id`),
  ADD CONSTRAINT `fk_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
